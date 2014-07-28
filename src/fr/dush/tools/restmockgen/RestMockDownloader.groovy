package fr.dush.tools.restmockgen

import groovy.json.JsonBuilder
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseDecorator
@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.1')
@Grapes([
        @GrabExclude('org.codehaus.groovy:groovy')
])
import groovyx.net.http.RESTClient

import java.nio.file.Path

@Slf4j
class RestMockDownloader {

    RESTClient restClient;
    String serverUrl
    Path localPath;

    RestMockDownloader(String serverUrl, Path localPath) {
        this.serverUrl = serverUrl
        this.localPath = localPath

        restClient = new RESTClient(serverUrl)
    }

    void get(String relativePath, Map<String, ?> query = [:], Closure callback = null) {
        log.info "Request ${relativePath} with ${query}"
        // Exec REST request
        def result = restClient.get(
                path: relativePath,
                query: query
        ) as HttpResponseDecorator

        // If success, write it in file
        if (result.success && result.status == 200) {
            log.debug "Request sucess! Got: ${result.data}"

            def file = localPath.resolve(relativePath)
            file.parent.toFile().mkdirs()

            def builder = new JsonBuilder(result.data)
            builder.writeTo(new FileWriter(file.toString())).close()

        } else {
            throw new RuntimeException("Could not get data from ${relativePath} (with query: ${query}): ${result.status} - ${result.data}")
        }

        // Call back
        if (callback) callback result.data
    }

    void download(String url) {
        def outputFile = localPath.resolve(url).toFile()
        if (!outputFile.exists()) {
            log.info "Downloading resource: ${url}"
            outputFile.getParentFile().mkdirs();

            def out = new BufferedOutputStream(new FileOutputStream(outputFile))
            out << new URL(serverUrl + url).openStream()
            out.close()
        }
    }
}
