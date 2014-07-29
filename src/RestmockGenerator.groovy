@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5')
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.5.1')
@Grapes([
        @GrabExclude('org.codehaus.groovy:groovy')
])
import fr.dush.tools.restmockgen.RestMockDownloader

import java.nio.file.Paths


// REST Downloader
def orig = 'http://dush-temp:8080/'
def dest = '../generated/rest'

def medima = new RestMockDownloader(orig, Paths.get(dest))

/** Download images and moVie details */
def downloaderCl = { data ->
    // Download actor images
    data.elements.mainActors.picture.each {
        it.each {
            medima.download withSize(it, 'THUMBS')
        }
    }

    // Download posters
    data.elements.poster.each {
        medima.download withSize(it, 'DISPLAY')
    }

    // Movie details
    data.elements.id.each {
        medima.get("api/movie/${it}.json")
    }
}

static String withSize(def url, String size = "DISPLAY") {
    "${url}?size=${size}"
}

// ** Start to download what we need
println "Starting to create a mock for rest service Medima: ${orig}..."

// Delete previous mock...
new File(dest).delete()


// Genres
medima.get('api/medias/genres.json')

// In Progress
medima.get('api/medias/inProgress.json', [:], { data ->
    // Download actor images
    data.mediaSummary.mainActors.picture.each {
        it.each {
            medima.download withSize(it, 'THUMBS')
        }
    }

    // Download posters
    data.mediaSummary.poster.each {
        medima.download withSize(it, 'DISPLAY')
    }

    // Movie details
    data.mediaSummary.id.each {
        medima.get("api/movie/${it}.json")
    }
})

// Random, last, alpha movies
for (sort in ['random', 'last', 'alpha', 'date']) {
    def query = [size: 20]
    if (sort == 'random') query.notNullFields = 'POSTER'

    medima.get(
            "api/movies/${sort}.json",
            query,
            downloaderCl
    )
}

// Playing...
medima.get('api/players/playing.json', [:], { data ->
    // Download actor images
    data.media.mainActors.picture.each {
        it.each {
            medima.download withSize(it, 'THUMBS')
        }
    }

    // Download posters
    data.media.poster.each {
        medima.download withSize(it, 'DISPLAY')
    }

    // Movie details
    data.media.id.each {
        medima.get("api/movie/${it}.json")
    }
})