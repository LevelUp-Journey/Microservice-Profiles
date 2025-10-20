# Competitive BC Refactoring Checklist

## Overview
This checklist guides the refactoring of the Competitive Bounded Context to remove misplaced leaderboard functionality and simplify architecture.

---

## Phase 1: Preparation & Analysis

- [ ] Review COMPETITIVE_BC_ANALYSIS.md and COMPETITIVE_BC_SUMMARY.txt
- [ ] Review current git status and create feature branch
- [ ] Document all dependencies between Competitive BC and other BCs
- [ ] Identify all REST API endpoints currently using affected code
- [ ] Create test plan for verifying changes
- [ ] Set up automated test suite for regression testing

---

## Phase 2: Delete Files (13 files to remove)

### Domain Model Files
- [ ] Delete: `/competitive/domain/model/valueobjects/LeaderboardPosition.java`
  - Test: Verify no other files import this
  - Impact: High - used in multiple files

- [ ] Delete: `/competitive/domain/model/commands/RecalculateLeaderboardPositionsCommand.java`
  - Test: Check no REST endpoints call this
  - Impact: Medium

- [ ] Delete: `/competitive/domain/model/queries/GetLeaderboardQuery.java`
  - Test: Check no REST endpoints call this
  - Impact: Medium

- [ ] Delete: `/competitive/domain/model/queries/GetUserRankingPositionQuery.java`
  - Test: Check no REST endpoints call this
  - Impact: Medium

- [ ] Delete: `/competitive/domain/services/RankCommandService.java`
  - Test: Check no implementations depend on this
  - Impact: Low

- [ ] Delete: `/competitive/domain/services/RankQueryService.java`
  - Test: Check no implementations depend on this
  - Impact: Low

### Application Layer Files
- [ ] Delete: `/competitive/application/internal/commandservices/RankCommandServiceImpl.java`
  - Test: Check ApplicationReadyEventHandler still works
  - Impact: Medium

- [ ] Delete: `/competitive/application/internal/queryservices/RankQueryServiceImpl.java`
  - Test: Verify no REST endpoints use this
  - Impact: Low

### REST Interface Files
- [ ] Delete: `/competitive/interfaces/rest/resources/LeaderboardEntryResource.java`
  - Test: Verify GET /leaderboard endpoint removed
  - Impact: Medium

- [ ] Delete: `/competitive/interfaces/rest/resources/UserRankingPositionResource.java`
  - Test: Verify GET /user/{userId}/position endpoint removed
  - Impact: Medium

- [ ] Delete: `/competitive/interfaces/rest/resources/RankResource.java`
  - Test: Verify no endpoints expose rank data
  - Impact: Low

- [ ] Delete: `/competitive/interfaces/rest/transform/LeaderboardEntryResourceFromEntityAssembler.java`
  - Test: Verify no other assemblers depend on this
  - Impact: Low

- [ ] Delete: `/competitive/interfaces/rest/transform/UserRankingPositionResourceFromEntityAssembler.java`
  - Test: Verify no other assemblers depend on this
  - Impact: Low

---

## Phase 3: Modify Existing Files (8 files)

### 1. CompetitiveProfile.java
**File Path:** `/competitive/domain/model/aggregates/CompetitiveProfile.java`

- [ ] Remove import: `LeaderboardPosition`
- [ ] Remove field (line 37-41):
  ```java
  @Embedded
  @AttributeOverrides({
          @AttributeOverride(name = "position", column = @Column(name = "leaderboard_position"))
  })
  private LeaderboardPosition leaderboardPosition;
  ```
- [ ] Remove method (line 103-110):
  ```java
  public void updateLeaderboardPosition(LeaderboardPosition position, Rank top500Rank)
  ```
- [ ] Remove method (line 126-128):
  ```java
  public boolean isTop500()
  ```
- [ ] Remove method (line 148-150):
  ```java
  public Integer getLeaderboardPosition()
  ```
- [ ] Update JavaDoc if referencing removed methods
- [ ] Update constructor if initializing leaderboardPosition
- [ ] Run tests: `mvn test -Dtest=CompetitiveProfileTest`

### 2. CompetitiveRank.java
**File Path:** `/competitive/domain/model/valueobjects/CompetitiveRank.java`

- [ ] Remove enum value (line 15):
  ```java
  TOP500(Integer.MAX_VALUE); // Special rank for top 500 users
  ```
- [ ] Verify `fromPoints()` method doesn't reference TOP500
- [ ] Update `getNextRank()` switch statement if needed
- [ ] Run tests: `mvn test -Dtest=CompetitiveRankTest`
- [ ] Verify no code tries to use CompetitiveRank.TOP500

### 3. CompetitiveProfileCommandServiceImpl.java
**File Path:** `/competitive/application/internal/commandservices/CompetitiveProfileCommandServiceImpl.java`

- [ ] Remove method (lines 140-166):
  ```java
  @Override
  @Transactional
  public Integer handle(RecalculateLeaderboardPositionsCommand command)
  ```
- [ ] Remove import: `RecalculateLeaderboardPositionsCommand`
- [ ] Remove import: `LeaderboardPosition`
- [ ] Remove any @Param annotations for top500Rank
- [ ] Run tests: `mvn test -Dtest=CompetitiveProfileCommandServiceImplTest`

### 4. CompetitiveProfileQueryServiceImpl.java
**File Path:** `/competitive/application/internal/queryservices/CompetitiveProfileQueryServiceImpl.java`

- [ ] Remove method (lines 55-64):
  ```java
  @Override
  @Transactional(readOnly = true)
  public List<CompetitiveProfile> handle(GetLeaderboardQuery query)
  ```
- [ ] Remove method (lines 80-108):
  ```java
  @Override
  @Transactional(readOnly = true)
  public Optional<Integer> handle(GetUserRankingPositionQuery query)
  ```
- [ ] Remove imports: `GetLeaderboardQuery`, `GetUserRankingPositionQuery`
- [ ] Remove unused imports (Pageable, PageRequest if only used by removed methods)
- [ ] Run tests: `mvn test -Dtest=CompetitiveProfileQueryServiceImplTest`

### 5. CompetitiveProfilesController.java
**File Path:** `/competitive/interfaces/rest/CompetitiveProfilesController.java`

- [ ] Remove endpoint method (lines 82-96):
  ```java
  @GetMapping("/leaderboard")
  public ResponseEntity<List<LeaderboardEntryResource>> getLeaderboard(...)
  ```
- [ ] Remove endpoint method (lines 107-129):
  ```java
  @GetMapping("/user/{userId}/position")
  public ResponseEntity<UserRankingPositionResource> getUserRankingPosition(...)
  ```
- [ ] Remove endpoint method (lines 192-197):
  ```java
  @PostMapping("/leaderboard/recalculate")
  public ResponseEntity<String> recalculateLeaderboard()
  ```
- [ ] Remove imports: `GetLeaderboardQuery`, `GetUserRankingPositionQuery`, `RecalculateLeaderboardPositionsCommand`
- [ ] Remove imports: `LeaderboardEntryResource`, `UserRankingPositionResource`
- [ ] Remove imports: `LeaderboardEntryResourceFromEntityAssembler`, `UserRankingPositionResourceFromEntityAssembler`
- [ ] Update controller JavaDoc to reflect remaining endpoints
- [ ] Run tests: `mvn test -Dtest=CompetitiveProfilesControllerTest`

### 6. CompetitiveProfileRepository.java
**File Path:** `/competitive/infrastructure/persistence/jpa/repositories/CompetitiveProfileRepository.java`

- [ ] Remove method (lines 53-54):
  ```java
  @Query("SELECT cp FROM CompetitiveProfile cp ORDER BY cp.totalPoints.value DESC, cp.createdAt ASC")
  List<CompetitiveProfile> findTopByOrderByTotalPointsDesc(Pageable pageable);
  ```
- [ ] Remove method (lines 61-62):
  ```java
  @Query("SELECT cp FROM CompetitiveProfile cp ORDER BY cp.totalPoints.value DESC, cp.createdAt ASC")
  List<CompetitiveProfile> findAllOrderedByTotalPoints();
  ```
- [ ] Remove method (lines 80-81):
  ```java
  @Query("SELECT COUNT(cp) FROM CompetitiveProfile cp WHERE cp.totalPoints.value > :points")
  Long countProfilesWithMorePoints(@Param("points") Integer points);
  ```
- [ ] Remove method (lines 91-92):
  ```java
  @Query("SELECT COUNT(cp) FROM CompetitiveProfile cp WHERE cp.totalPoints.value = :points AND cp.createdAt < :createdAt")
  Long countProfilesWithSamePointsButEarlier(@Param("points") Integer points, @Param("createdAt") java.util.Date createdAt);
  ```
- [ ] Remove method (lines 100-101):
  ```java
  @Query("SELECT cp FROM CompetitiveProfile cp WHERE cp.currentRank = :rank ORDER BY cp.totalPoints.value DESC")
  List<CompetitiveProfile> findTop500Profiles(@Param("rank") Rank rank);
  ```
- [ ] Remove import: `Pageable`, `PageRequest` if only used by removed methods
- [ ] Run tests: `mvn test -Dtest=CompetitiveProfileRepositoryTest`

### 7. RankRepository.java
**File Path:** `/competitive/infrastructure/persistence/jpa/repositories/RankRepository.java`

- [ ] Remove method (line 42):
  ```java
  @Query("SELECT r FROM Rank r WHERE r.minimumPoints <= :points AND r.rankName != 'TOP500' ORDER BY r.minimumPoints DESC")
  Optional<Rank> findRankByPoints(@Param("points") Integer points);
  ```
- [ ] Verify no code calls `findRankByPoints()`
- [ ] Run tests: `mvn test -Dtest=RankRepositoryTest`

### 8. Rank.java
**File Path:** `/competitive/domain/model/entities/Rank.java`

- [ ] Review if any methods reference TOP500
- [ ] Check `getNextRank()` method if it handles TOP500
- [ ] Run tests: `mvn test -Dtest=RankTest`

---

## Phase 4: Simplify Rank Reference Data (Optional but Recommended)

- [ ] Create `RankReferenceDataService` in infrastructure layer
- [ ] Create `RankReferenceDataInitializer` for seeding
- [ ] Update `ApplicationReadyEventHandler` to use new service
- [ ] Test seeding still works on application startup

---

## Phase 5: Testing

### Unit Tests
- [ ] Run all Competitive BC tests: `mvn test -Dtest=*Competitive*`
- [ ] Run all test suites: `mvn test`
- [ ] Check test coverage hasn't decreased significantly

### Integration Tests
- [ ] Test profile creation still works
- [ ] Test score sync triggers profile update
- [ ] Test rank calculation based on points
- [ ] Test get profile by user ID
- [ ] Test get users by rank
- [ ] Test all remaining REST endpoints

### Regression Tests
- [ ] Verify no remaining code references LeaderboardPosition
- [ ] Verify no remaining code references removed commands/queries
- [ ] Verify no remaining code calls removed repository methods
- [ ] Search codebase: `grep -r "LeaderboardPosition" src/`
- [ ] Search codebase: `grep -r "TOP500" src/` (should not find in Competitive BC)
- [ ] Search codebase: `grep -r "RecalculateLeaderboardPositionsCommand" src/`
- [ ] Search codebase: `grep -r "GetLeaderboardQuery" src/`
- [ ] Search codebase: `grep -r "GetUserRankingPositionQuery" src/`

---

## Phase 6: Documentation Updates

- [ ] Update README.md if it mentions leaderboard endpoints
- [ ] Update API documentation (Swagger/OpenAPI)
- [ ] Update architecture diagrams
- [ ] Update bounded context documentation
- [ ] Document migration to Leaderboard BC
- [ ] Update team documentation

---

## Phase 7: Git & Commit

- [ ] Stage all changes: `git add .`
- [ ] Review changes: `git diff --staged`
- [ ] Create comprehensive commit message
- [ ] Commit with: `git commit -m "..."`
- [ ] Push to feature branch: `git push origin feature/...`

---

## Phase 8: Code Review

- [ ] Request code review from team
- [ ] Address review comments
- [ ] Ensure all tests pass in CI/CD
- [ ] Get approval for merge

---

## Phase 9: Next Steps - Create Leaderboard BC

After merging, create new Leaderboard BC with:
- [ ] LeaderboardPosition value object (moved from Competitive)
- [ ] Leaderboard aggregate
- [ ] Leaderboard commands (RecalculateLeaderboardPositionsCommand)
- [ ] Leaderboard queries (GetLeaderboardQuery, GetUserRankingPositionQuery)
- [ ] LeaderboardCommandService & LeaderboardQueryService
- [ ] Leaderboard REST controller
- [ ] Leaderboard resources and assemblers
- [ ] Event handlers to react to CompetitiveProfileUpdatedEvent
- [ ] ACL to read competitive profile data

---

## Success Criteria

- [ ] All 13 files successfully deleted
- [ ] All 8 files successfully modified
- [ ] All unit tests pass
- [ ] No compilation errors
- [ ] No references to removed classes/methods
- [ ] All REST endpoints still work (minus leaderboard ones)
- [ ] Competitive BC is focused on competitive profiles only
- [ ] Code is ready for Leaderboard BC extraction

---

## Rollback Plan

If issues arise:
1. `git reset --hard HEAD`
2. `git clean -fd`
3. Investigate specific failures
4. Create new branch and retry with specific changes

---

## Notes

- Make changes incrementally and test after each phase
- Commit frequently for easier rollback if needed
- Update tests as you go, not after
- Consider pairing with another developer for review
- Document any unexpected dependencies found

