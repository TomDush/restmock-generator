@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.1')
@Grapes([
        @GrabExclude('org.codehaus.groovy:groovy')
])
import fr.dush.tools.restmockgen.RestMockDownloader

import java.nio.file.Paths

// How to make Intellij resolving Grab (groovy) imports?

println "Requesting Medima...."

new File('../generated/rest').delete()

def medima = new RestMockDownloader('http://dush-temp:8080/', Paths.get('../generated/rest'))

medima.get(
        'api/movies/random.json',
        [notNullFields: 'POSTER', size: 20],
        { data ->
            // Download actor images
            data.elements.mainActors.picture.each {
                it.each {
                    medima.download withSize(it)
                }
            }

            // Download posters
            data.elements.poster.each {
                medima.download withSize(it)
            }

            // Movie details
            data.elements.id.each {
                medima.get("api/movie/53b87e0cb1b59ca7ddcd6538.json")
            }
        }
)

String withSize(def url, String size = "DISPLAY") {
    "${url}?size=${size}"
}