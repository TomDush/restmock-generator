dependencies {
    runtime('org.codehaus.groovy.modules.http-builder:http-builder:0.5.1') {
        excludes 'xalan'
        excludes 'xml-apis'
        excludes 'groovy'
    }
}