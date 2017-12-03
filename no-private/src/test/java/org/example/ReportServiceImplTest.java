/*
 * Copyright 2017 Shinya Mochida
 * 
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.example;

import com.example.ReportServiceImpl;
import com.example.dao.GameDao;
import com.example.object.GameScore;
import com.example.object.UserScore;
import com.example.resource.TimeRepository;
import com.example.resource.UserExternalRepository;
import com.example.util.Pair;
import com.example.value.GameId;
import com.example.value.TeamId;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static com.example.value.UserId.userId;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportServiceImplTest {

    @Nested
    @DisplayName("getDailyReport(LocalDate)")
    class GetDailyReportTest {

        @Nested
        @DisplayName("パラメーターの日付が")
        class DateTest {

            @DisplayName("本日の場合は")
            @Nested
            class Today {

                private ReportServiceImpl service;

                private LocalDate today;

                @BeforeEach
                void setup() {
                    today = LocalDate.now();
                    service = reportService()
                            .timeRepository((TimeRepository timeRepository) -> when(timeRepository.today()).thenReturn(today))
                            .gameDao()
                            .userExternalRepository();
                }

                @DisplayName("レポートが0件になる")
                @Test
                void test1() {
                    final Map<TeamId, LongSummaryStatistics> actual = service.getDailyReport(today);

                    assertTrue(actual.isEmpty());
                }
            }

            @DisplayName("翌日の場合は")
            @Nested
            class Tomorrow {

                private ReportServiceImpl service;

                private LocalDate date;

                @BeforeEach
                void setup() {
                    final LocalDate today = LocalDate.now();
                    date = today.plusDays(1L);
                    service = reportService()
                            .timeRepository((TimeRepository timeRepository) -> when(timeRepository.today()).thenReturn(today))
                            .gameDao()
                            .userExternalRepository();
                }

                @DisplayName("レポートが0件になる")
                @Test
                void test2() {
                    final Map<TeamId, LongSummaryStatistics> actual = service.getDailyReport(date);

                    assertTrue(actual.isEmpty());
                }
            }

            @Nested
            @DisplayName("前日の場合に")
            class PreviousDay {

                @BeforeEach
                void setup() {
                    final TimeRepository mock = mock(TimeRepository.class);
                    when(mock.today()).thenReturn(LocalDate.now());
                    serviceBuilder = reportService().timeRepository(mock);
                }

                private MockReportServiceGameDaoBuilder serviceBuilder;

                private LocalDate yesterday() {
                    return LocalDate.now().minusDays(1L);
                }

                @Nested
                @DisplayName("プレイしたユーザーがいない場合は")
                class NoPlayer {

                    final LocalDate date = yesterday();

                    private ReportServiceImpl service;

                    @BeforeEach
                    void setup() {
                        service = serviceBuilder
                                .gameDao((GameDao dao) -> when(dao.dailyUserScoreList(date)).thenReturn(Collections.emptyList()))
                                .userExternalRepository(
                                        (UserExternalRepository repo) -> when(repo.findPremiumUser(anyIterable())).thenReturn(Collections.emptyList()));
                    }

                    @DisplayName("レポートが0件になる")
                    @Test
                    void test1() {
                        final Map<TeamId, LongSummaryStatistics> actual = service.getDailyReport(date);

                        assertTrue(actual.isEmpty());
                    }
                }

                @Nested
                @DisplayName("プレイしたユーザーのうち")
                class UserScoresTest {

                    private MockUserExternalRepoBuilder serviceBuilder;

                    private UserScore userScore(final long team, final long user, final long game, final int score) {
                        return new UserScore(new TeamId(team), userId(user), new GameScore(new GameId(game), score));
                    }

                    @BeforeEach
                    void setup() {
                        final List<UserScore> userScores = Arrays.asList(
                                userScore(1L, 1L, 1, 10),
                                userScore(1L, 1L, 2, 20),
                                userScore(1L, 2L, 1, 40),
                                userScore(2L, 3L, 1, 0),
                                userScore(3L, 4L, 1, 10),
                                userScore(3L, 4L, 2, -10)
                        );
                        serviceBuilder = PreviousDay.this.serviceBuilder
                                .gameDao((GameDao dao) -> when(dao.dailyUserScoreList(yesterday())).thenReturn(userScores));
                    }

                    @DisplayName("プレミアムユーザーが0人の場合は")
                    @Nested
                    class NoPremiumUser {

                        private ReportServiceImpl service;

                        @BeforeEach
                        void setup() {
                            service = serviceBuilder.userExternalRepository(
                                    (UserExternalRepository repository) -> when(repository.findPremiumUser(anyIterable())).thenReturn(Collections.emptyList()));
                        }

                        @DisplayName("レポートが0件になる")
                        @Test
                        void test1() {
                            final Map<TeamId, LongSummaryStatistics> actual = service.getDailyReport(yesterday());

                            assertTrue(actual.isEmpty());
                        }
                    }

                    @DisplayName("プレミアムユーザーの合計得点が0点以下のものしかいない場合は")
                    @Nested
                    class SumIs0OrLower {

                        private ReportServiceImpl service;

                        @BeforeEach
                        void setup() {
                            service = serviceBuilder.userExternalRepository(
                                    (UserExternalRepository repository) -> when(repository.findPremiumUser(anyIterable())).thenReturn(
                                            Arrays.asList(userId(3L), userId(4L))));
                        }

                        @DisplayName("レポートが0件になる")
                        @Test
                        void test1() {
                            final Map<TeamId, LongSummaryStatistics> actual = service.getDailyReport(yesterday());
                            assertTrue(actual.isEmpty());
                        }
                    }

                    @DisplayName("プレミアムユーザーの得点のみ")
                    @Nested
                    class OnlyPremiumUser {

                        private ReportServiceImpl service;

                        @BeforeEach
                        void setup() {
                            service = serviceBuilder.userExternalRepository(
                                    (UserExternalRepository repository) -> when(repository.findPremiumUser(anyIterable())).thenReturn(
                                            Arrays.asList(userId(1L), userId(3L))));
                        }

                        @DisplayName("集計される")
                        @TestFactory
                        Iterable<DynamicTest> test() {
                            final Map<TeamId, LongSummaryStatistics> actual = service.getDailyReport(yesterday());
                            final Map<TeamId, Long> map = actual.entrySet().stream()
                                    .map(e -> Pair.of(e.getKey(), e.getValue().getSum()))
                                    .collect(groupingBy(Pair::getLeft, summingLong(Pair::getRight)));
                            return Arrays.asList(
                                    dynamicTest("件数は1件", () -> assertEquals(1, map.size())),
                                    dynamicTest("TeamId=1 の合計点が30", () -> assertEquals(30L, map.get(new TeamId(1L)).longValue()))
                            );
                        }
                    }
                }
            }
        }
    }

    private static MockReportServiceTimeRepoBuilder reportService() {
        return t -> g -> u -> new ReportServiceImpl(Objects.requireNonNull(t), Objects.requireNonNull(g), Objects.requireNonNull(u));
    }

    private static <T> Consumer<T> consumer(final Consumer<? super T> consumer) {
        return consumer::accept;
    }
}

interface MockReportServiceTimeRepoBuilder {
    default MockReportServiceGameDaoBuilder timeRepository() {
        return timeRepository(mock(TimeRepository.class));
    }
    default MockReportServiceGameDaoBuilder timeRepository(final Consumer<TimeRepository> timeRepo) {
        final TimeRepository mock = mock(TimeRepository.class);
        timeRepo.accept(mock);
        return timeRepository(mock);
    }
    MockReportServiceGameDaoBuilder timeRepository(final TimeRepository time);
}

interface MockReportServiceGameDaoBuilder {
    default MockUserExternalRepoBuilder gameDao() {
        return gameDao(mock(GameDao.class));
    }
    default MockUserExternalRepoBuilder gameDao(final Consumer<GameDao> dao) {
        final GameDao mock = mock(GameDao.class);
        dao.accept(mock);
        return gameDao(mock);
    }
    MockUserExternalRepoBuilder gameDao(final GameDao dao);
}

interface MockUserExternalRepoBuilder {
    default ReportServiceImpl userExternalRepository() {
        return userExternalRepository(mock(UserExternalRepository.class));
    }
    default ReportServiceImpl userExternalRepository(final Consumer<UserExternalRepository> repository) {
        final UserExternalRepository mock = mock(UserExternalRepository.class);
        repository.accept(mock);
        return userExternalRepository(mock);
    }
    ReportServiceImpl userExternalRepository(final UserExternalRepository userExternal);
}
