# Competitive Bounded Context - Comprehensive Analysis

## 1. FILE STRUCTURE AND LOCATIONS

### Domain Model (13 files)
#### Value Objects (4 files):
- `/competitive/domain/model/valueobjects/CompetitiveRank.java` - Enum with rank definitions and point thresholds
- `/competitive/domain/model/valueobjects/CompetitiveUserId.java` - User ID reference
- `/competitive/domain/model/valueobjects/TotalPoints.java` - Points accumulation value object
- `/competitive/domain/model/valueobjects/LeaderboardPosition.java` - Position ranking value object

#### Entities (1 file):
- `/competitive/domain/model/entities/Rank.java` - Database entity for rank reference data

#### Aggregates (1 file):
- `/competitive/domain/model/aggregates/CompetitiveProfile.java` - Root aggregate for user competitive data

#### Commands (5 files):
- `/competitive/domain/model/commands/CreateCompetitiveProfileCommand.java`
- `/competitive/domain/model/commands/UpdateCompetitivePointsCommand.java`
- `/competitive/domain/model/commands/SyncCompetitiveProfileFromScoresCommand.java`
- `/competitive/domain/model/commands/RecalculateLeaderboardPositionsCommand.java`
- `/competitive/domain/model/commands/SeedRanksCommand.java`

#### Queries (5 files):
- `/competitive/domain/model/queries/GetCompetitiveProfileByUserIdQuery.java`
- `/competitive/domain/model/queries/GetLeaderboardQuery.java`
- `/competitive/domain/model/queries/GetUserRankingPositionQuery.java`
- `/competitive/domain/model/queries/GetUsersByRankQuery.java`
- `/competitive/domain/model/queries/GetAllCompetitiveProfilesQuery.java`

#### Domain Services (4 interface files):
- `/competitive/domain/services/CompetitiveProfileCommandService.java`
- `/competitive/domain/services/CompetitiveProfileQueryService.java`
- `/competitive/domain/services/RankCommandService.java`
- `/competitive/domain/services/RankQueryService.java`

### Application Layer (6 files)
#### Command Services (2 files):
- `/competitive/application/internal/commandservices/CompetitiveProfileCommandServiceImpl.java`
- `/competitive/application/internal/commandservices/RankCommandServiceImpl.java`

#### Query Services (2 files):
- `/competitive/application/internal/queryservices/CompetitiveProfileQueryServiceImpl.java`
- `/competitive/application/internal/queryservices/RankQueryServiceImpl.java`

#### Event Handlers (2 files):
- `/competitive/application/internal/eventhandlers/ApplicationReadyEventHandler.java`
- `/competitive/application/internal/eventhandlers/ScoreUpdatedEventHandler.java`

### Infrastructure Layer (3 files)
#### Repositories (2 files):
- `/competitive/infrastructure/persistence/jpa/repositories/CompetitiveProfileRepository.java`
- `/competitive/infrastructure/persistence/jpa/repositories/RankRepository.java`

#### ACL / External Services (1 file):
- `/competitive/application/internal/outboundservices/acl/ExternalScoresService.java`

### Interfaces Layer (10 files)
#### REST Controller (1 file):
- `/competitive/interfaces/rest/CompetitiveProfilesController.java`

#### Resources/DTOs (5 files):
- `/competitive/interfaces/rest/resources/CompetitiveProfileResource.java`
- `/competitive/interfaces/rest/resources/CreateCompetitiveProfileResource.java`
- `/competitive/interfaces/rest/resources/LeaderboardEntryResource.java`
- `/competitive/interfaces/rest/resources/UserRankingPositionResource.java`
- `/competitive/interfaces/rest/resources/RankResource.java`

#### Assemblers/Transformers (5 files):
- `/competitive/interfaces/rest/transform/CompetitiveProfileResourceFromEntityAssembler.java`
- `/competitive/interfaces/rest/transform/CreateCompetitiveProfileCommandFromResourceAssembler.java`
- `/competitive/interfaces/rest/transform/LeaderboardEntryResourceFromEntityAssembler.java`
- `/competitive/interfaces/rest/transform/UserRankingPositionResourceFromEntityAssembler.java`
- `/competitive/interfaces/rest/transform/RankResourceFromEntityAssembler.java`

---

## 2. CURRENT RESPONSIBILITIES OF COMPETITIVE BC

### Core Competitive Profile Management
1. **Create competitive profiles** - New users enter at BRONZE rank with 0 points
2. **Maintain competitive standings** - Track total points and current rank
3. **Synchronize with Scores BC** - React to ScoreUpdatedEvent and update competitive data
4. **Rank progression** - Calculate appropriate rank (BRONZE→SILVER→GOLD→PLATINUM→DIAMOND→MASTER→GRANDMASTER→TOP500) based on points
5. **Rank seeding** - Initialize reference data from CompetitiveRank enum

### Leaderboard Management (SHOULD BE IN LEADERBOARD BC)
1. **Leaderboard position calculation** - Rank users globally by points
2. **TOP500 rank assignment** - Grant special TOP500 rank to top 500 users
3. **Leaderboard position retrieval** - Query users' positions in leaderboard
4. **Global leaderboard display** - Fetch paginated leaderboard rankings
5. **Position recalculation** - Recalculate all positions and TOP500 designations

### Query Operations
1. Get profile by user ID
2. Get leaderboard (paginated)
3. Get user ranking position
4. Get users by rank
5. Get all competitive profiles (admin)

### Reference Data Management
1. Store Rank entities with minimum points thresholds
2. Query rank entities by name/enum value
3. Seed ranks at application startup

---

## 3. CODE VIOLATING BOUNDED CONTEXT SEPARATION

### CRITICAL: Leaderboard Functionality Should Be in Leaderboard BC

#### Problem Areas:

1. **LeaderboardPosition Value Object** (CompetitiveBC Domain)
   - File: `/competitive/domain/model/valueobjects/LeaderboardPosition.java`
   - Issue: Position calculation and TOP500 qualification logic belong to Leaderboard BC
   - Should be: Moved to Leaderboard BC as a shared value object

2. **RecalculateLeaderboardPositionsCommand** (CompetitiveBC Domain)
   - File: `/competitive/domain/model/commands/RecalculateLeaderboardPositionsCommand.java`
   - Issue: Global leaderboard recalculation is Leaderboard BC responsibility
   - Should be: Moved to Leaderboard BC domain model

3. **GetLeaderboardQuery** (CompetitiveBC Domain)
   - File: `/competitive/domain/model/queries/GetLeaderboardQuery.java`
   - Issue: Leaderboard retrieval is Leaderboard BC responsibility
   - Should be: Moved to Leaderboard BC domain model

4. **GetUserRankingPositionQuery** (CompetitiveBC Domain)
   - File: `/competitive/domain/model/queries/GetUserRankingPositionQuery.java`
   - Issue: User ranking position is Leaderboard BC responsibility
   - Should be: Moved to Leaderboard BC domain model

5. **LeaderboardEntryResource** (CompetitiveBC Interfaces)
   - File: `/competitive/interfaces/rest/resources/LeaderboardEntryResource.java`
   - Issue: Leaderboard entry representation belongs to Leaderboard BC
   - Should be: Moved to Leaderboard BC interfaces

6. **UserRankingPositionResource** (CompetitiveBC Interfaces)
   - File: `/competitive/interfaces/rest/resources/UserRankingPositionResource.java`
   - Issue: User ranking position representation belongs to Leaderboard BC
   - Should be: Moved to Leaderboard BC interfaces

7. **updateLeaderboardPosition() Method** (CompetitiveProfile Aggregate)
   - Location: `/competitive/domain/model/aggregates/CompetitiveProfile.java` (lines 103-110)
   - Issue: Leaderboard position update logic in competitive profile
   - Should be: Removed from CompetitiveProfile; position should be managed by Leaderboard BC

8. **getLeaderboardPosition() Getter** (CompetitiveProfile Aggregate)
   - Location: `/competitive/domain/model/aggregates/CompetitiveProfile.java` (lines 148-150)
   - Issue: Aggregate exposes leaderboard-specific data
   - Should be: Removed; leaderboard position is Leaderboard BC concern

9. **leaderboardPosition Field** (CompetitiveProfile Aggregate)
   - Location: `/competitive/domain/model/aggregates/CompetitiveProfile.java` (line 41)
   - Issue: Competitive profile storing leaderboard position
   - Should be: Removed; position calculated and stored in Leaderboard BC

10. **isTop500() Method** (CompetitiveProfile Aggregate)
    - Location: `/competitive/domain/model/aggregates/CompetitiveProfile.java` (lines 126-128)
    - Issue: TOP500 is a leaderboard concept, not competitive rank
    - Should be: Removed; TOP500 rank assignment should be Leaderboard BC responsibility

11. **TOP500 Rank Enum Value** (CompetitiveRank Enum)
    - Location: `/competitive/domain/model/valueobjects/CompetitiveRank.java` (line 15)
    - Issue: TOP500 is leaderboard-specific, not a true competitive rank
    - Should be: Removed from competitive ranks; kept only in Leaderboard BC

12. **TOP500 Rank Assignment Logic** (CompetitiveProfileCommandServiceImpl)
    - Location: `/competitive/application/internal/commandservices/CompetitiveProfileCommandServiceImpl.java` (lines 140-166)
    - Issue: RecalculateLeaderboardPositionsCommand handler performs leaderboard calculations
    - Should be: Moved to Leaderboard BC command service

13. **Leaderboard Query Handling** (CompetitiveProfileQueryServiceImpl)
    - Location: `/competitive/application/internal/queryservices/CompetitiveProfileQueryServiceImpl.java` (lines 55-64, 80-108)
    - Issue: GetLeaderboardQuery and GetUserRankingPositionQuery are leaderboard concerns
    - Should be: Moved to Leaderboard BC query service

14. **Leaderboard Entry Assemblers** (CompetitiveBC Interfaces)
    - File: `/competitive/interfaces/rest/transform/LeaderboardEntryResourceFromEntityAssembler.java`
    - File: `/competitive/interfaces/rest/transform/UserRankingPositionResourceFromEntityAssembler.java`
    - Issue: Leaderboard representation logic
    - Should be: Moved to Leaderboard BC

15. **Leaderboard API Endpoints** (CompetitiveProfilesController)
    - Location: `/competitive/interfaces/rest/CompetitiveProfilesController.java` (lines 77-129, 187-197)
    - Issue: Leaderboard endpoints should be in Leaderboard BC controller
    - Methods to move:
      - `getLeaderboard()` (lines 82-96)
      - `getUserRankingPosition()` (lines 107-129)
      - `recalculateLeaderboard()` (lines 192-197)
    - Methods to keep: `getCompetitiveProfileByUserId()`, `getUsersByRank()`, `syncCompetitiveProfile()`

16. **Leaderboard Repository Queries** (CompetitiveProfileRepository)
    - Location: `/competitive/infrastructure/persistence/jpa/repositories/CompetitiveProfileRepository.java`
    - Issue: Several queries are leaderboard-specific
    - Methods to move:
      - `findTopByOrderByTotalPointsDesc()` (lines 53-54)
      - `findAllOrderedByTotalPoints()` (lines 61-62)
      - `countProfilesWithMorePoints()` (lines 80-81)
      - `countProfilesWithSamePointsButEarlier()` (lines 91-92)
      - `findTop500Profiles()` (lines 100-101)
    - Context: These queries are used for leaderboard calculations, not competitive profile management

---

## 4. DDD STRUCTURE VIOLATIONS

### Rank Reference Data Management Issue

**Problem**: The `Rank` entity is treated as a reference/master data table, but it's modeled as an aggregate root.

1. **RankCommandService and RankCommandServiceImpl**
   - Purpose: Only handles SeedRanksCommand (initialization)
   - Issue: After seeding, ranks are immutable reference data, not an aggregate with business logic
   - Violation: Treating immutable reference data as a full DDD aggregate

2. **RankQueryService and RankQueryServiceImpl**
   - Issue: Query service for reference data seems unnecessary
   - Better approach: Direct repository access for lookup tables

3. **Separate Rank Services**
   - Issue: Having separate command/query services for reference data adds unnecessary complexity
   - Better approach: Single lookup service or repository-level queries

**Recommendation**: 
- Treat `Rank` as a reference/lookup table, not a full aggregate
- Move from domain services to infrastructure layer lookup
- Keep repository queries for rank resolution but remove command/query service pattern

---

## 5. WHAT SHOULD STAY IN COMPETITIVE BC

### Core Responsibilities
1. **CompetitiveProfile Aggregate Root** (refined version without leaderboard position)
   - Managing user's competitive standing
   - Tracking total points
   - Rank progression based on points
   - BRONZE through GRANDMASTER ranks (but NOT TOP500)

2. **CompetitiveRank Enum** (without TOP500)
   - Define 7 competitive ranks (BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER)
   - Rank progression logic
   - Minimum points thresholds for each rank

3. **Domain Services**
   - CompetitiveProfileCommandService/Impl
   - CompetitiveProfileQueryService/Impl

4. **Commands**
   - CreateCompetitiveProfileCommand
   - UpdateCompetitivePointsCommand
   - SyncCompetitiveProfileFromScoresCommand
   - (Remove: RecalculateLeaderboardPositionsCommand)

5. **Queries**
   - GetCompetitiveProfileByUserIdQuery
   - GetUsersByRankQuery
   - GetAllCompetitiveProfilesQuery
   - (Remove: GetLeaderboardQuery, GetUserRankingPositionQuery)

6. **Value Objects**
   - CompetitiveUserId
   - TotalPoints
   - CompetitiveRank (7 ranks only)
   - (Remove: LeaderboardPosition)

7. **REST Endpoints** (refined)
   - GET `/api/v1/competitive/profiles/user/{userId}` - Get profile
   - POST `/api/v1/competitive/profiles/user/{userId}/sync` - Sync with Scores BC
   - GET `/api/v1/competitive/profiles/rank/{rank}` - Get users by rank

8. **Event Handling**
   - ScoreUpdatedEventHandler - Sync competitive profiles when scores change
   - ApplicationReadyEventHandler - Seed initial rank data

9. **Infrastructure**
   - CompetitiveProfileRepository (refined queries)
   - ExternalScoresService (ACL to Scores BC)
   - Repository methods for:
     - Find profile by user ID
     - Check if profile exists
     - Find by rank
     - Find by points threshold

---

## 6. WHAT SHOULD BE REMOVED FROM COMPETITIVE BC

### Files to Delete (13 items)

1. **LeaderboardPosition.java**
   - Path: `/competitive/domain/model/valueobjects/LeaderboardPosition.java`
   - Reason: Leaderboard BC responsibility

2. **RecalculateLeaderboardPositionsCommand.java**
   - Path: `/competitive/domain/model/commands/RecalculateLeaderboardPositionsCommand.java`
   - Reason: Leaderboard BC responsibility

3. **GetLeaderboardQuery.java**
   - Path: `/competitive/domain/model/queries/GetLeaderboardQuery.java`
   - Reason: Leaderboard BC responsibility

4. **GetUserRankingPositionQuery.java**
   - Path: `/competitive/domain/model/queries/GetUserRankingPositionQuery.java`
   - Reason: Leaderboard BC responsibility

5. **LeaderboardEntryResource.java**
   - Path: `/competitive/interfaces/rest/resources/LeaderboardEntryResource.java`
   - Reason: Leaderboard BC responsibility

6. **UserRankingPositionResource.java**
   - Path: `/competitive/interfaces/rest/resources/UserRankingPositionResource.java`
   - Reason: Leaderboard BC responsibility

7. **LeaderboardEntryResourceFromEntityAssembler.java**
   - Path: `/competitive/interfaces/rest/transform/LeaderboardEntryResourceFromEntityAssembler.java`
   - Reason: Leaderboard BC responsibility

8. **UserRankingPositionResourceFromEntityAssembler.java**
   - Path: `/competitive/interfaces/rest/transform/UserRankingPositionResourceFromEntityAssembler.java`
   - Reason: Leaderboard BC responsibility

9. **RankCommandService.java**
   - Path: `/competitive/domain/services/RankCommandService.java`
   - Reason: Reference data doesn't need command pattern

10. **RankQueryService.java**
    - Path: `/competitive/domain/services/RankQueryService.java`
    - Reason: Reference data lookup is infrastructure concern

11. **RankCommandServiceImpl.java**
    - Path: `/competitive/application/internal/commandservices/RankCommandServiceImpl.java`
    - Reason: Move seeding logic to infrastructure or initialization

12. **RankQueryServiceImpl.java**
    - Path: `/competitive/application/internal/queryservices/RankQueryServiceImpl.java`
    - Reason: Reference data lookup is infrastructure concern

13. **RankResource.java**
    - Path: `/competitive/interfaces/rest/resources/RankResource.java`
    - Reason: Rank reference data shouldn't be exposed via REST API

### Code Changes Required in Existing Files

#### CompetitiveProfile.java (Aggregate Root)
**Remove lines 37-41, 103-110, 126-128, 148-150**
```java
// REMOVE:
@Embedded
@AttributeOverrides({
        @AttributeOverride(name = "position", column = @Column(name = "leaderboard_position"))
})
private LeaderboardPosition leaderboardPosition;

// REMOVE method:
public void updateLeaderboardPosition(LeaderboardPosition position, Rank top500Rank)
public boolean isTop500()
public Integer getLeaderboardPosition()
```

#### CompetitiveRank.java (Enum)
**Remove line 15**
```java
// REMOVE:
TOP500(Integer.MAX_VALUE); // Special rank for top 500 users
```

#### CompetitiveProfileCommandServiceImpl.java
**Remove lines 140-166** (handle RecalculateLeaderboardPositionsCommand method)

#### CompetitiveProfileQueryServiceImpl.java
**Remove lines 55-64, 80-108** (handle GetLeaderboardQuery and GetUserRankingPositionQuery methods)

#### CompetitiveProfilesController.java
**Remove lines 77-129, 187-197** (getLeaderboard, getUserRankingPosition, recalculateLeaderboard endpoints)

#### CompetitiveProfileRepository.java
**Remove methods lines 53-54, 61-62, 80-81, 91-92, 100-101**
```java
// REMOVE:
findTopByOrderByTotalPointsDesc()
findAllOrderedByTotalPoints()
countProfilesWithMorePoints()
countProfilesWithSamePointsButEarlier()
findTop500Profiles()
```

#### RankRepository.java
**Remove line 42**
```java
// REMOVE:
findRankByPoints() method (leaderboard ranking concern)
```

---

## 7. RESTRUCTURING RECOMMENDATIONS

### Create Leaderboard BC with:

1. **Domain Model**
   - LeaderboardPosition value object (moved from Competitive BC)
   - Leaderboard aggregate (new)
   - Commands: RecalculateLeaderboardPositionsCommand, UpdateLeaderboardPositionCommand
   - Queries: GetLeaderboardQuery, GetUserRankingPositionQuery
   - Events: LeaderboardRecalculatedEvent, UserRankingChangedEvent

2. **ACL to Competitive BC**
   - CompetitiveProfileReadService (interface for reading competitive data)
   - CompetitiveProfileLeaderboardData (DTO containing userId, points, rank)

3. **Services**
   - LeaderboardCommandService - Recalculate positions, assign TOP500
   - LeaderboardQueryService - Retrieve leaderboard, user position

4. **REST Controller**
   - Move leaderboard endpoints from Competitive controller
   - GET `/api/v1/leaderboard`
   - GET `/api/v1/leaderboard/user/{userId}/position`
   - POST `/api/v1/leaderboard/recalculate` (admin)

5. **Event Handling**
   - Listen to CompetitiveProfileUpdatedEvent
   - Trigger leaderboard recalculation when needed

### Simplify Rank Reference Data

**Option 1: Infrastructure Component (Recommended)**
```
infrastructure/
  persistence/
    references/
      RankReferenceDataService.java (simple lookup)
      RankReferenceDataInitializer.java (seeding)
```

**Option 2: Keep in Domain but Simplify**
- Remove RankCommandService/QueryService interfaces
- Create single RankLookupService for queries
- Initialize via ApplicationReadyEventHandler with direct repository save

---

## 8. SUMMARY TABLE

| Aspect | Current State | Issue | Action |
|--------|---------------|-------|--------|
| LeaderboardPosition | In Competitive BC domain | Leaderboard concern | Move to Leaderboard BC |
| Leaderboard queries | In Competitive BC | Leaderboard concern | Move to Leaderboard BC |
| TOP500 rank | In CompetitiveRank enum | Leaderboard concept | Remove from enum, handle in Leaderboard BC |
| Leaderboard endpoints | In CompetitiveProfilesController | Leaderboard concern | Move to Leaderboard controller |
| Rank services | Command/Query services | Over-engineered for reference data | Simplify or delete |
| CompetitiveProfile aggregate | Contains leaderboard position | Mixed concerns | Remove leaderboard-related methods |
| Repository queries | Leaderboard-specific queries | Leaderboard concern | Remove or move to Leaderboard BC |
| Event handling | ScoreUpdatedEvent → Sync | Correct | Keep as is |

---

## 9. MIGRATION STRATEGY

### Phase 1: Preparation
1. Create Leaderboard BC structure
2. Create ACL interfaces for Competitive BC data access
3. Identify and document all dependencies

### Phase 2: Extract Leaderboard Functionality
1. Create LeaderboardPosition in Leaderboard BC
2. Create leaderboard commands and queries
3. Implement leaderboard services
4. Create leaderboard REST controller

### Phase 3: Update Competitive BC
1. Remove leaderboard-related code
2. Simplify CompetitiveProfile aggregate
3. Remove TOP500 from CompetitiveRank
4. Update CompetitiveProfileController
5. Simplify repository queries

### Phase 4: Integration
1. Implement event publishing from Competitive BC
2. Implement Leaderboard BC event handlers
3. Set up inter-BC communication
4. Test end-to-end workflows

### Phase 5: Cleanup
1. Delete removed files
2. Update tests
3. Update documentation
4. Verify all endpoints work

