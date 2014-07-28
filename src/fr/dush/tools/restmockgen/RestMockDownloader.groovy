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
    Path rootPath;

    RestMockDownloader(String url, Path rootPath) {
        restClient = new RESTClient(url)
        this.rootPath = rootPath;
    }

    void get(String relativePath, Map<String, ?> query) {
        log.debug "Get path ${relativePath} with ${query}"
        // Exec REST request
        def result = restClient.get(
                path: relativePath,
                query: query
        ) as HttpResponseDecorator

        // If success, write it in file
        if (result.success && result.statusCode == 200) {
            log.info "Request sucess! Got: ${result.data}"

            def builder = new JsonBuilder()
            builder.content = result.data
            builder.writeTo new FileWriter(rootPath.resolve(relativePath).toString())
            
        } else {
            throw new RuntimeException("Could not get data from ${relativePath} (with query: ${query}): ${result.statusCode} - ${result.data}")
        }

    }
}
