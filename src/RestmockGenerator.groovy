import groovyx.net.http.RESTClient;
import groovyx.net.http.HttpResponseDecorator;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequestInterceptor;
import groovy.json.JsonSlurper;
import static groovyx.net.http.Method.*;
import static groovyx.net.http.ContentType.*;

@Grab(value = 'org.codehaus.groovy:groovy-all:1.7.5', initClass = false)
@Grapes([
        @Grab(group = 'org.codehaus.groovy.modules.http-builder',
                module = 'http-builder', version = '0.5.1'),
        @GrabExclude('org.codehaus.groovy:groovy')
])
def greeting = "hello world"

println "Hello World! I'm a groovy script!"
