package lots.slotsazinotopora.triqaz.logics.main;


import java.util.List;

import io.reactivex.Single;

public interface IMainIteractor {
    Single<List<Movie>> getPopularMovie(int page);
    Single<List<Movie>> getTopRated(int page);
}