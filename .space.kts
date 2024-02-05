job("Build and publish") {
    container(displayName = "Run publish script", image = "gradle") {
        kotlinScript { api ->
            api.gradlew("test build")
            api.gradlew("publish")
        }
    }
}