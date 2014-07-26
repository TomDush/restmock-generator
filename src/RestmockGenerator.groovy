@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.1')
@Grapes([
        @GrabExclude('org.codehaus.groovy:groovy')
])
import groovyx.net.http.RESTClient


// How to make Intellij resolving Grab (groovy) imports?

def greeting = "world"
println "Hello ${greeting} !"

def medima = new RESTClient('http://dush-temp:8080/api/')

println 'Get random list...'
def resp = medima.get(
        path: 'movies/random.json',
        query: [notNullFields: 'POSTER', size: 20]
)

List movies = resp.data.elements
println "${movies.size()} random movies: "
movies.each { println "\t- ${it.title}" }
println ""