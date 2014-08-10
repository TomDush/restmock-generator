@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.1')
@Grapes([
        @GrabExclude('org.codehaus.groovy:groovy')
])
import fr.dush.tools.restmockgen.RestMockDownloader
import org.apache.commons.io.FileUtils

import java.nio.file.Paths

// ** REST Downloader
//def orig = 'http://dush-temp:8080/'
def orig = 'http://localhost:8090/'
def dest = '../generated/rest'

medima = new RestMockDownloader(orig, Paths.get(dest))

// ** UTILS

void loadMovie(def summary) {
    // Download actor images
    summary.mainActors.picture.each {
        medima.download it, [size: 'THUMBS']
    }

    // Download posters
    medima.download summary.poster, [size: 'DISPLAY']

    // Movie details
    medima.get("api/movie/${summary.id}.json", [:], { data ->
        // Backdrops
        data.backdrops.each { medima.download it, [size: 'DISPLAY'] }
    })

}

// ** Start to download what we need
println "Starting to create a mock for rest service Medima: ${orig}..."

// Delete previous mock...
FileUtils.deleteDirectory(new File(dest))

// Genres
medima.get('api/medias/genres.json')

// In Progress
medima.get('api/medias/inProgress.json', [:], { data ->
    data.mediaSummary.each { loadMovie it }
})

// Random, last, alpha movies
for (sort in ['random', 'last', 'alpha', 'date']) {
    def query = [size: 20]
    if (sort == 'random') query.notNullFields = 'POSTER'
    if (sort == 'last') query.seen = 'UNSEEN'

    medima.get(
            "api/movies/${sort}.json",
            query,
            { data -> data.elements.each { loadMovie it } }
    )
}

// Playing...
medima.get('api/players/playing.json', [:], { data ->
    data.media.each { loadMovie it }
})