# Competitive Bounded Context - Executive Summary

## Overview
Analysis of the Competitive Bounded Context (BC) in the LevelUp-Journey Profiles microservice, identifying architectural violations and providing remediation recommendations.

**Analysis Date:** October 20, 2025
**Status:** Complete - Ready for Implementation
**Severity:** High - Multiple Bounded Context Separation Violations

---

## Key Findings

### Critical Issue
The Competitive BC contains **significant leaderboard functionality** that violates bounded context separation principles. Leaderboard management should be in a dedicated Leaderboard BC.

### Numbers
- **Total Files:** 40 Java files
- **Files with Issues:** 21 files (52%)
- **Files to Delete:** 13 files
- **Files to Modify:** 8 files
- **Files to Keep:** 19 files

---

## Problem Statement

### Current Architecture (WRONG)
```
Competitive BC:
  ├─ CompetitiveProfile (aggregate)
  ├─ CompetitiveRank (with TOP500 enum value)
  ├─ LeaderboardPosition (value object)
  ├─ RecalculateLeaderboardPositionsCommand
  ├─ GetLeaderboardQuery
  ├─ GetUserRankingPositionQuery
  ├─ Leaderboard REST endpoints
  ├─ Leaderboard-specific repository queries
  ├─ Rank reference data management
  └─ ... other code
```

### Target Architecture (CORRECT)
```
Competitive BC:
  ├─ CompetitiveProfile (aggregate) - no leaderboard position
  ├─ CompetitiveRank (7 ranks only, no TOP500)
  ├─ Rank progression logic
  ├─ Competitive profile management
  └─ Synchronization with Scores BC

Leaderboard BC (NEW):
  ├─ LeaderboardPosition (value object)
  ├─ Leaderboard (aggregate)
  ├─ RecalculateLeaderboardPositionsCommand
  ├─ GetLeaderboardQuery
  ├─ GetUserRankingPositionQuery
  ├─ Leaderboard REST endpoints
  ├─ Leaderboard-specific calculations
  └─ TOP500 rank assignment logic
```

---

## Top 5 Critical Violations

### 1. TOP500 Rank in CompetitiveRank Enum (CRITICAL)
- **Location:** `CompetitiveRank.java` (line 15)
- **Issue:** TOP500 is a leaderboard concept, not a competitive rank
- **Impact:** HIGH - Violates fundamental BC separation
- **Fix:** Remove TOP500 from enum; create in Leaderboard BC

### 2. LeaderboardPosition Value Object (CRITICAL)
- **Location:** `domain/model/valueobjects/LeaderboardPosition.java`
- **Issue:** Entirely leaderboard responsibility
- **Impact:** HIGH - Used in multiple Competitive BC files
- **Fix:** Move to Leaderboard BC

### 3. Leaderboard Queries in Competitive BC (CRITICAL)
- **Locations:** `GetLeaderboardQuery.java`, `GetUserRankingPositionQuery.java`
- **Issue:** Global rankings are Leaderboard BC responsibility
- **Impact:** HIGH - Fundamental BC violation
- **Fix:** Move to Leaderboard BC

### 4. Leaderboard REST Endpoints (MEDIUM)
- **Location:** `CompetitiveProfilesController.java` (3 endpoints)
- **Issues:**
  - `GET /leaderboard` - Global leaderboard display
  - `GET /user/{userId}/position` - User ranking position
  - `POST /leaderboard/recalculate` - Recalculate all positions
- **Impact:** MEDIUM - API design violation
- **Fix:** Move to Leaderboard BC controller

### 5. Over-engineered Rank Reference Data (MEDIUM)
- **Issue:** Using command/query service pattern for static reference data
- **Components:** RankCommandService, RankQueryService, implementations
- **Impact:** MEDIUM - Architectural over-engineering
- **Fix:** Simplify to infrastructure lookup service

---

## Impact Analysis

### Data Loss Risk
- NO data loss if correctly refactored
- Leaderboard position data currently in `competitive_profiles` table should be migrated to separate leaderboard table

### Performance Impact
- Slight improvement from simplified Competitive BC
- Leaderboard BC can optimize recalculation operations

### API Breaking Changes
- YES - 3 REST endpoints will move to different controller
- **Clients should expect:** `/api/v1/leaderboard/*` instead of `/api/v1/competitive/profiles/leaderboard/*`

### Dependencies
- Scores BC depends on Competitive BC (OK)
- Leaderboard BC will depend on Competitive BC (NEW)
- Competitive BC should publish events for Leaderboard BC

---

## Deliverables Generated

### 1. COMPETITIVE_BC_ANALYSIS.md (21 KB)
Comprehensive analysis including:
- Complete file structure and locations
- Current responsibilities breakdown
- Detailed list of 16 bounded context violations
- DDD structure violations
- What should stay vs. be removed
- Migration strategy with 5 phases

### 2. COMPETITIVE_BC_SUMMARY.txt (11 KB)
Quick reference including:
- Overview of issues
- Files to delete (13)
- Files to modify (8)
- DDD violations explained
- Architecture improvements before/after

### 3. REFACTORING_CHECKLIST.md (12 KB)
Step-by-step implementation guide with:
- 9 phases of refactoring
- Specific line numbers for deletion/modification
- Testing strategy per phase
- Success criteria and rollback plan

### 4. FILE_MAPPING.txt (12 KB)
Complete file directory with:
- Absolute paths to all files
- Keep/delete/modify categorization
- Dependencies and impacts
- Quick reference for each action

---

## Implementation Roadmap

### Phase 1: Preparation (1-2 days)
- [ ] Review all documentation
- [ ] Set up test suite
- [ ] Create feature branch
- [ ] Identify all dependencies

### Phase 2: Delete Files (1 day)
- [ ] Delete 13 files with impact verification
- [ ] Run tests after each deletion group

### Phase 3: Modify Files (2 days)
- [ ] Modify 8 files, removing leaderboard code
- [ ] Remove TOP500 from enum
- [ ] Update imports and documentation

### Phase 4: Simplify Reference Data (Optional - 1 day)
- [ ] Delete Rank services or move to infrastructure
- [ ] Create RankLookupService

### Phase 5: Testing & Review (2 days)
- [ ] Unit tests
- [ ] Integration tests
- [ ] Regression testing
- [ ] Code review

### Phase 6: Create Leaderboard BC (Separate task)
- [ ] Extract moved components
- [ ] Implement new Leaderboard services
- [ ] Create REST controller
- [ ] Set up event handling

**Total Effort:** 7-10 days (team of 1-2 developers)

---

## Risk Assessment

### Low Risk
- Deleting unused reference data files (RankResource, etc.)
- Removing leaderboard-only methods from services
- Test coverage for removed code can be deleted

### Medium Risk
- Modifying CompetitiveProfile aggregate (core entity)
- Removing Repository queries (verify no usage first)
- Removing from CompetitiveRank enum

### High Risk
- Moving leaderboard functionality without proper inter-BC communication
- Inconsistent state between competitive profiles and leaderboard
- Clients expecting old endpoints

### Mitigation
- Follow checklist systematically
- Test after each major change
- Have rollback plan ready
- Create Leaderboard BC in parallel feature branch before merging this one

---

## Benefits

### Immediate
1. Cleaner Competitive BC focused on profiles and rank progression
2. Reduced complexity in domain model
3. Better separation of concerns
4. Easier to test and maintain

### Long-term
1. Leaderboard BC can evolve independently
2. Better scaling possibilities for leaderboard recalculation
3. Cleaner API design
4. Foundation for additional ranking systems (seasonal, tournament, etc.)

---

## Recommendations

### Priority 1 (Must Do)
1. Implement Phase 1-3 of refactoring
2. Remove leaderboard code from Competitive BC
3. Extract to Leaderboard BC

### Priority 2 (Should Do)
1. Simplify Rank reference data handling
2. Create RankLookupService in infrastructure
3. Remove command/query pattern from reference data

### Priority 3 (Nice to Have)
1. Create shared domain events for inter-BC communication
2. Implement proper event-driven architecture
3. Add integration tests for BC interactions

---

## Sign-Off Checklist

- [ ] Architecture review completed
- [ ] Team understands migration path
- [ ] All documentation approved
- [ ] Test plan accepted
- [ ] Timeline and resources confirmed
- [ ] Risk mitigation strategy understood
- [ ] Ready to proceed with implementation

---

## Questions & Answers

**Q: Will this break existing API clients?**
A: Yes, leaderboard endpoints will move. Recommend API versioning or migration period.

**Q: Do we lose any data?**
A: No, if migration is done correctly. Leaderboard position data should be moved, not deleted.

**Q: Can we do this gradually?**
A: Yes, but create Leaderboard BC in parallel to avoid temporary duplication.

**Q: What about existing tests?**
A: Update/delete tests for removed code. Create new tests for Leaderboard BC.

**Q: Timeline to complete?**
A: 1-2 weeks with 1-2 developers, following the provided checklist.

---

## Contact & Support

- Refer to COMPETITIVE_BC_ANALYSIS.md for detailed technical analysis
- Refer to REFACTORING_CHECKLIST.md for step-by-step instructions
- Refer to FILE_MAPPING.txt for file locations and dependencies
- Use COMPETITIVE_BC_SUMMARY.txt for quick reference

---

**Ready to proceed with refactoring implementation.**
