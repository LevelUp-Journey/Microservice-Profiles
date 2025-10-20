# Bounded Contexts Architecture - LevelUp Journey Platform

## Overview

This document describes the architecture and integration of the bounded contexts in the Profiles microservice following Domain-Driven Design (DDD) principles.

## Bounded Contexts

### 1. Scores BC (Scores Bounded Context)
**Responsibility**: Manages individual scores and points earned by users

**Core Capabilities**:
- Create and track individual score records
- Calculate total points per user
- Provide score history and analytics
- Publish events when scores change

**Aggregate Root**: `Score`

**Key Value Objects**:
- `ScoreUserId` - User identifier
- `Points` - Points value for a score
- `Source` - Source/reason for the score

**Events**:
- `ScoreUpdatedEvent` - Published when a user's score is created/updated

**ACL Facade**: `ScoresContextFacade`
- `getTotalPointsByUserId(String userId)` - Get user's total points
- `getAllUserTotalPoints()` - Get all users' total points (for bulk operations)
- `userHasScores(String userId)` - Check if user has any scores

---

### 2. Competitive BC (Competitive Bounded Context)
**Responsibility**: Defines competitive ranks and manages user rank progression based on points

**Core Capabilities**:
- Assign competitive ranks based on point thresholds
- Track rank progression (Bronze → Silver → Gold → Platinum → Diamond → Master → Grandmaster)
- Calculate points needed for next rank
- Sync with Scores BC to maintain up-to-date rankings

**Aggregate Root**: `CompetitiveProfile`

**Entities**:
- `Rank` - Reference data entity for rank definitions

**Key Value Objects**:
- `CompetitiveUserId` - User identifier
- `TotalPoints` - Total accumulated points
- `CompetitiveRank` - Enum (BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER)

**Events**:
- `ScoreUpdatedEvent` (consumed from Scores BC)

**Commands**:
- `CreateCompetitiveProfileCommand`
- `UpdateCompetitivePointsCommand`
- `SyncCompetitiveProfileFromScoresCommand`

**External Dependencies**:
- **Scores BC** (via `ExternalScoresService`)
  - Fetches total points for users
  - Checks if users have scores

**REST Endpoints**:
- `GET /api/v1/competitive/profiles/user/{userId}` - Get competitive profile
- `GET /api/v1/competitive/profiles/rank/{rank}` - Get users by rank
- `POST /api/v1/competitive/profiles/user/{userId}/sync` - Sync with Scores BC

---

### 3. Leaderboard BC (Leaderboard Bounded Context)
**Responsibility**: Generates global user rankings based on total points

**Core Capabilities**:
- Generate global leaderboard ordered by points
- Calculate real-time user positions
- Maintain TOP 500 rankings
- Provide efficient paginated queries

**Aggregate Root**: `LeaderboardEntry`

**Key Value Objects**:
- `LeaderboardUserId` - User identifier
- `LeaderboardPosition` - Position in global ranking (1-indexed)
- `LeaderboardPoints` - Total points for ranking

**Events**:
- `ScoreUpdatedEvent` (consumed from Scores BC)

**Commands**:
- `UpdateLeaderboardEntryCommand` - Update/create entry with new points
- `RecalculateLeaderboardPositionsCommand` - Recalculate all positions

**External Dependencies**:
- **Scores BC** (via `ExternalScoresService`)
  - Fetches total points for users
  - Bulk fetch all users' points

**ACL Facade**: `LeaderboardContextFacade`
- `getUserPosition(String userId)` - Get user's leaderboard position
- `getTopNUsers(Integer limit)` - Get top N users
- `getTop500UserIds()` - Get TOP 500 user IDs
- `isUserInTop500(String userId)` - Check if user is in TOP 500

**REST Endpoints**:
- `GET /api/v1/leaderboard` - Get paginated leaderboard
- `GET /api/v1/leaderboard/top500` - Get TOP 500
- `GET /api/v1/leaderboard/user/{userId}` - Get user's position
- `POST /api/v1/leaderboard/recalculate` - Recalculate positions (Admin)

---

### 4. Profiles BC (Profiles Bounded Context)
**Responsibility**: Manages user profile information

**Note**: This is a separate bounded context managing general profile data (bio, avatar, etc.)

---

## Integration Architecture

### Data Flow Diagram

```
┌─────────────┐
│  Scores BC  │
│             │
│ - Creates   │
│   Scores    │
│ - Calculates│
│   Totals    │
└──────┬──────┘
       │
       │ publishes
       │ ScoreUpdatedEvent
       │
       ├─────────────────┬─────────────────┐
       │                 │                 │
       ▼                 ▼                 ▼
┌─────────────┐  ┌──────────────┐  ┌──────────────┐
│Competitive  │  │ Leaderboard  │  │   Profiles   │
│     BC      │  │      BC      │  │      BC      │
│             │  │              │  │              │
│ - Assigns   │  │ - Generates  │  │ - Stores     │
│   Ranks     │  │   Rankings   │  │   Bio/Avatar │
│ - Tracks    │  │ - Calculates │  │              │
│   Progress  │  │   Positions  │  │              │
└─────────────┘  └──────────────┘  └──────────────┘
```

### ACL (Anti-Corruption Layer) Integration

#### 1. Scores BC → Competitive BC
**Purpose**: Competitive BC needs total points to assign ranks

**Implementation**:
- **Provider**: Scores BC
  - Interface: `ScoresContextFacade`
  - Implementation: `ScoresContextFacadeImpl`
- **Consumer**: Competitive BC
  - ACL Service: `ExternalScoresService`
  - Usage: Fetch total points when creating/updating competitive profiles

**Flow**:
```
CompetitiveProfileCommandService
    → ExternalScoresService.fetchTotalPointsByUserId()
        → ScoresContextFacade.getTotalPointsByUserId()
            → ScoreRepository.sumPointsByUserId()
```

#### 2. Scores BC → Leaderboard BC
**Purpose**: Leaderboard BC needs total points to generate rankings

**Implementation**:
- **Provider**: Scores BC
  - Interface: `ScoresContextFacade`
  - Implementation: `ScoresContextFacadeImpl`
- **Consumer**: Leaderboard BC
  - ACL Service: `ExternalScoresService`
  - Event Handler: `ScoreUpdatedEventHandler`

**Flow (Real-time)**:
```
ScoreCommandService.handle(CreateScoreCommand)
    → publishes ScoreUpdatedEvent
        → ScoreUpdatedEventHandler (Leaderboard BC)
            → LeaderboardCommandService.handle(UpdateLeaderboardEntryCommand)
                → Updates position in leaderboard
```

**Flow (Bulk)**:
```
LeaderboardCommandService.handle(RecalculateLeaderboardPositionsCommand)
    → ExternalScoresService.fetchAllUserTotalPoints()
        → ScoresContextFacade.getAllUserTotalPoints()
            → Recalculates all positions
```

#### 3. Leaderboard BC → Competitive BC (Future Integration)
**Purpose**: Competitive BC could check if user is in TOP 500

**Implementation**:
- **Provider**: Leaderboard BC
  - Interface: `LeaderboardContextFacade`
  - Implementation: `LeaderboardContextFacadeImpl`
- **Consumer**: Competitive BC (if needed)
  - ACL Service: `ExternalLeaderboardService` (to be created if needed)

---

## Event-Driven Architecture

### Events Flow

```
User Action (e.g., completes task)
    ↓
Scores BC creates Score
    ↓
Scores BC publishes ScoreUpdatedEvent
    ↓
    ├─→ Competitive BC (ScoreUpdatedEventHandler)
    │   └─→ Updates CompetitiveProfile with new points
    │       └─→ Recalculates rank based on points
    │
    └─→ Leaderboard BC (ScoreUpdatedEventHandler)
        └─→ Updates LeaderboardEntry with new points
            └─→ Recalculates position in leaderboard
```

### Event Handlers

**Competitive BC**:
- `ScoreUpdatedEventHandler` - Listens to `ScoreUpdatedEvent` from Scores BC
  - Updates competitive profile points
  - Recalculates rank

**Leaderboard BC**:
- `ScoreUpdatedEventHandler` - Listens to `ScoreUpdatedEvent` from Scores BC
  - Updates leaderboard entry points
  - Recalculates position

---

## Key Design Principles Applied

### 1. Bounded Context Isolation
✅ Each BC has clear boundaries and responsibilities
✅ No direct dependencies between BCs
✅ Communication only through ACL facades

### 2. Anti-Corruption Layer (ACL)
✅ ACL facades protect domain models from external changes
✅ Simple data types in ACL interfaces (String, Integer, Boolean)
✅ Value objects stay internal to each BC

### 3. CQRS (Command Query Responsibility Segregation)
✅ Separate Command and Query services
✅ Commands modify state
✅ Queries are read-only with `@Transactional(readOnly = true)`

### 4. Domain Events
✅ `ScoreUpdatedEvent` enables loose coupling
✅ Asynchronous event handling with `@Async`
✅ Event-driven synchronization between BCs

### 5. Ubiquitous Language
✅ Each BC has its own vocabulary:
  - Scores BC: "Score", "Points", "Source"
  - Competitive BC: "CompetitiveProfile", "Rank", "Rank Progression"
  - Leaderboard BC: "LeaderboardEntry", "Position", "TOP 500"

---

## Separation of Concerns

| Concern | Responsible BC | NOT Responsible |
|---------|---------------|-----------------|
| **Individual score tracking** | Scores BC | ❌ Competitive BC, ❌ Leaderboard BC |
| **Total points calculation** | Scores BC | ❌ Competitive BC, ❌ Leaderboard BC |
| **Rank assignment** | Competitive BC | ❌ Scores BC, ❌ Leaderboard BC |
| **Rank progression** | Competitive BC | ❌ Scores BC, ❌ Leaderboard BC |
| **Global ranking** | Leaderboard BC | ❌ Scores BC, ❌ Competitive BC |
| **Position calculation** | Leaderboard BC | ❌ Scores BC, ❌ Competitive BC |
| **TOP 500 designation** | Leaderboard BC | ❌ Competitive BC |

---

## Benefits of This Architecture

### 1. Scalability
- Each BC can be scaled independently
- Async event handling prevents blocking
- Efficient queries optimized per BC

### 2. Maintainability
- Clear boundaries reduce cognitive load
- Changes in one BC don't affect others
- Easy to understand and modify

### 3. Testability
- Each BC can be tested in isolation
- Mock ACL facades for testing
- Clear contracts via interfaces

### 4. Flexibility
- Easy to add new BCs
- Can replace implementation without affecting others
- Support for future microservices migration

---

## Future Enhancements

### 1. Caching
- Cache leaderboard positions for performance
- Cache user rankings in Competitive BC
- Cache total points from Scores BC

### 2. Batch Processing
- Scheduled leaderboard recalculation
- Batch rank updates
- Optimized bulk operations

### 3. Microservices Migration
- Each BC is ready to be extracted as a microservice
- ACL facades become REST APIs
- Events become message queue events

---

## Summary

This architecture implements DDD bounded contexts with:
- ✅ Clear separation of responsibilities
- ✅ Anti-Corruption Layer for integration
- ✅ Event-driven synchronization
- ✅ CQRS pattern
- ✅ Domain events for loose coupling
- ✅ Ready for future microservices migration

**Result**: A well-structured, maintainable, and scalable system that properly separates concerns between scoring, competitive ranking, and global leaderboards.
