allprojects {
    group = "me.crylonz.mendingenchant"
    version = "1.7.0"
}

tasks.register("printVersion") {
    doLast {
        println(project.version)
    }
}
