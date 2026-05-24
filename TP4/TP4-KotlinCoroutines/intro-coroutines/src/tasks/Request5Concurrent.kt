package tasks

import contributors.*
import kotlinx.coroutines.*

suspend fun loadContributorsConcurrent(
    service: GitHubService,
    req: RequestData
): List<User> = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    log("[CONCURRENT] Scope thread antes dos async: ${Thread.currentThread().name}")

    val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
        async(Dispatchers.Default) {
            log("starting loading for ${repo.name}")
            delay(1000)
            service.getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }
    deferreds.awaitAll().flatten().aggregate()
}
