plugins {
    id("org.openrewrite.build.recipe-library") version "latest.release"
}

group = "io.moderne"
description = "Recipe layouts."

dependencies {
    // The bom version can also be set to a specific version
    // https://github.com/openrewrite/rewrite-recipe-bom/releases
    implementation(platform("org.openrewrite.recipe:rewrite-recipe-bom:latest.release"))

    implementation("org.openrewrite:rewrite-core")
    implementation("org.openrewrite.recipe:rewrite-spring")
    implementation("org.openrewrite.recipe:rewrite-static-analysis")

    implementation("org.apache.commons:commons-text:latest.release")
}
