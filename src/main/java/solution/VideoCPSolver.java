package solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import static com.google.ortools.sat.CpSolverStatus.FEASIBLE;
import static com.google.ortools.sat.CpSolverStatus.OPTIMAL;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;

public class VideoCPSolver {

    static {
        Loader.loadNativeLibraries();
    }

    public List<Server> solve(InputAdapter inputAdapter) {

        var nVideo = inputAdapter.nVideo();
        var nCache = inputAdapter.nCache();
        var nServer = nCache + 1;
        var nRequest = inputAdapter.nRequest();
        var videoSize = inputAdapter.videoSizes();
        var requests = inputAdapter.requests();
        var endpointToCacheLatency = inputAdapter.endpointToCacheLatency();

        // build the model
        // http://google.github.io/or-tools/javadoc/com/google/ortools/sat/LinearExpr.html
        var model = new CpModel();

        // create variables 1 : cache, video, in or not
        // nCache + 1 is because the data center is considered as a cache also
        Table<Integer, Integer, IntVar> cacheVideoVariables = HashBasedTable.create();
        for (int i = 0; i < nServer; i++) {
            for (int j = 0; j < nVideo; j++) {
                cacheVideoVariables.put(i, j, model.newBoolVar("C" + i + "V" + j));
            }
        }
        // constrain 1: all videos in one cache should <= cache's capacity
        /*

         */
        for (int i = 0; i < nCache; i++) {
            IntVar[] videos = new IntVar[nVideo]; // [video1, video2, ..., videon] videos in cache i
            for (int j = 0; j < nVideo; j++) {
                videos[j] = cacheVideoVariables.get(i, j); // i=1, j = 5 -> "C1V5"
            }
            model.addLessOrEqual(LinearExpr.scalProd(videos, videoSize), inputAdapter.cacheSize());
        }

        // create variables 2: request, cache, request using this cache or not
        Table<Integer, Integer, IntVar> reqCacheVariables = HashBasedTable.create();
        for (int i = 0; i < nRequest; i++) {
            for (int j = 0; j < nServer; j++) {
                reqCacheVariables.put(i, j, model.newBoolVar("R" + i + "C" + j));
            }
        }
        // constrain 2: the request should take from and only from 1 cache (data center include)
        for (int i = 0; i < nRequest; i++) {
            IntVar[] requestToCache = new IntVar[nServer];
            for (int j = 0; j < nServer; j++) {
                requestToCache[j] = reqCacheVariables.get(i, j);
            }
            model.addEquality(LinearExpr.sum(requestToCache), 1);
        }

        // the cell is the score for request i get video from the cache j
        Table<Integer, Integer, Integer> reqCacheScore = HashBasedTable.create();
        // set score for each request
        for (int i = 0; i < nRequest; i++) {
            var request = requests.get(i);
            var endpointId = request.endpointId();
            var videoId = request.videoId();
            var count = request.count();

            // video get from data center(the id is the nCache) has score = 0
            reqCacheScore.put(i, nCache, 0);
            for (int j = 0; j < nCache; j++) {
                // if request go through this cache, this cache should contain the required video
                model.addLessOrEqual(reqCacheVariables.get(i, j), cacheVideoVariables.get(j, videoId));
                /*
                if ep_cache[thise,j]<0:
                    scoreRC[i,j] =  0
                else:
                    scoreRC[i,j] = thiscount* ( ep_center[thise] - ep_cache[thise,j])
                 */
                var cacheLatency = endpointToCacheLatency.get(endpointId, j);
                if (cacheLatency == null) {
                    // the endpoint is not connected with this cache
                    reqCacheScore.put(i, j, 0);
                } else {
                    reqCacheScore.put(i, j,
                        count * (endpointToCacheLatency.get(endpointId, nCache) - cacheLatency));
                }
            }
        }

        //Objective: the sum of score for each request when it goes through the cache
        // table to array
        var intVars = new IntVar[nRequest * nServer];
        var scores = new int[nRequest * nServer];
        for (int i = 0; i < nRequest; i++) {
            for (int j = 0; j < nServer; j++) {
                var p = i * nRequest + j;
                intVars[p] = reqCacheVariables.get(i, j);
                scores[p] = reqCacheScore.get(i, j);
            }
        }
        model.maximize(LinearExpr.scalProd(intVars, scores));

        System.err.println(model.model().getConstraintsCount());
        // solve
        var solver = new CpSolver();
        var status = solver.solve(model);

        // result
        if (status == OPTIMAL || status == FEASIBLE) {
            var objectiveValue = solver.objectiveValue();
            System.out.println("Total decrease = " + objectiveValue);
            var score = 1000 * objectiveValue / inputAdapter.totalCounts();
            System.out.println("Score = " + score);

            var cacheToVideos = new HashMap<Integer, Set<Integer>>();

            for (int i = 0; i < nCache; i++) {
                for (int j = 0; j < nVideo; j++) {
                    if (solver.booleanValue(cacheVideoVariables.get(i, j))) {
                        var videos = cacheToVideos.getOrDefault(i, new HashSet<>());
                        videos.add(j);
                        cacheToVideos.put(i, videos);
                    }
                }
            }
            return cacheToVideos.entrySet().stream()
                .map(entry -> new Server(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        } else {
            System.err.println("Can't solve problem" + status);
            return Collections.emptyList();
        }
    }
}
