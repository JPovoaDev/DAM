package tasks

import contributors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

fun loadContributorsCallbacks(
    service: GitHubService, req: RequestData,
    updateResults: (List<User>) -> Unit) {

    service.getOrgReposCall(req.org).onResponse { responseRepos ->
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()

        /*val allUsers = mutableListOf<User>()
                for (repo in repos) {
                    service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
                        logUsers(repo, responseUsers)
                        val users = responseUsers.bodyList()
                        allUsers += users
                    }
                }*/
        /*
        Na solução atual, muitas requisições são iniciadas simultaneamente,
        o que diminui o tempo total de carregamento. No entanto, o resultado
        não é carregado. Isso ocorre porque a função de retorno de chamada
        `updateResults()` é chamada logo após o início de todas as requisições
        de carregamento, antes que a lista `allUsers` tenha sido preenchida com os
         dados.

        val allUsers = mutableListOf<User>()
        for ((index, repo) in repos.withIndex()) {   // #1
            service.getRepoContributorsCall(req.org, repo.name)
                .onResponse { responseUsers ->
                    logUsers(repo, responseUsers)
                    val users = responseUsers.bodyList()
                    allUsers += users
                    if (index == repos.lastIndex) {    // #2
                        updateResults(allUsers.aggregate())
                    }
                }
        } Primeiro, iteramos sobre a lista de repositórios com um índice (#1).
        Em seguida, a partir de cada retorno de chamada, verificamos se é a última
         iteração (#2), se for o caso, o resultado é atualizado.
         No entanto, esse código também não atinge o objetivo.


         Como as requisições de carregamento são iniciadas simultaneamente, não há garantia
         de que o resultado da última requisição seja o último a ser processado.
         Os resultados podem chegar em qualquer ordem.
         Portanto, se você comparar o índice atual com o último índice como condição
         para a conclusão do processamento, corre o risco de perder os resultados de
         alguns repositórios.
         Se a requisição que processa o último repositório retornar mais rápido
         do que algumas requisições anteriores (o que é provável), todos os resultados
          das requisições que demorarem mais serão perdidos.
          Uma maneira de corrigir isso é introduzir um índice e verificar se todos
           os repositórios já foram processados.
        */
        val allUsers = Collections.synchronizedList(mutableListOf<User>())
        val numberOfProcessed = AtomicInteger()
        val countDownLatch = CountDownLatch(repos.size)
        for (repo in repos) {
            service.getRepoContributorsCall(req.org, repo.name)
                .onResponse { responseUsers ->
                    logUsers(repo, responseUsers)
                    val users = responseUsers.bodyList()
                    allUsers += users
                    if (numberOfProcessed.incrementAndGet() == repos.size) {
                        updateResults(allUsers.aggregate())
                    }
                    countDownLatch.countDown()
                }
        }
        /*
        Este codigo usa uma versao sincrinozada da lista e um AtomicInteger(), pois,
        em geral nao ha garatia que diferentes callbaks que o processo getRepoContributors()
        iram sempre ser chamados da mesma thread.
        */

        countDownLatch.await()
        updateResults(allUsers.aggregate())
    }
    /*
    O resultado é entao atualizado da main thread. Esta é mais direto do que
    delegar a logica as threads filhas
    */

}

inline fun <T> Call<T>.onResponse(
    crossinline callback: (Response<T>) -> Unit
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            log.error("Call failed", t)
        }
    })
}
