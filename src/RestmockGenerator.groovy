@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.1')
@Grapes([
        @GrabExclude('org.codehaus.groovy:groovy')
])
import fr.dush.tools.restmockgen.RestMockDownloader

import java.nio.file.Paths

// How to make Intellij resolving Grab (groovy) imports?

def greeting = "world"
println "Hello ${greeting} !"

def medima = new RestMockDownloader('http://dush-temp:8080/api/', Paths.get('target/rest'))

println 'Get random list...'
medima.get(
        'movies/random.json',
        [notNullFields: 'POSTER', size: 20]
)