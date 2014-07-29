@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.1')
@Grapes([
        @GrabExclude('org.codehaus.groovy:groovy')
])
import fr.dush.tools.restmockgen.RestMockDownloader

import java.nio.file.Paths


// ** REST Downloader
def orig = 'http://dush-temp:8080/'
def dest = '../generated/rest'

final static def medima = new RestMockDownloader(orig, Paths.get(dest))

// ** UTILS

/** Download images and moVie details */
static String withSize(def url, String size = "DISPLAY") {
    "${url}?size=${size}"
}

void loadMovie(def summary) {
    // Download actor images
    summary.mainActors.picture.each {
        medima.download withSize(it, 'THUMBS')
    }

    // Download posters
    medima.download withSize(summary.poster, 'DISPLAY')

    // Movie details
    medima.get("api/movie/${summary.id}.json", [:], { data ->
        // Backdrops
        data.backdrops.each {medima.download it}
    })

}

// ** Start to download what we need
println "Starting to create a mock for rest service Medima: ${orig}..."

// Delete previous mock...
new File(dest).delete()

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