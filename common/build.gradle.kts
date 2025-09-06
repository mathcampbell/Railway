/*
 * Steam 'n' Rails
 * Copyright (c) 2022-2024 The Railways Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

loom {
    accessWidenerPath = file("src/main/resources/railways.accesswidener")
}

architectury {
    common {
        for(p in rootProject.subprojects) {
            if(p != project) {
                this@common.add(p.name)
            }
        }
    }
}

dependencies {
    // Keep Fabric loader for @Environment annotations used in common
    compileOnly("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")

    // --- Create v6 on 1.21.1: compile-only APIs for common code ---
    // Create (slim) – v6 on 1.21.1
    compileOnly("com.simibubi.create:create-${property("minecraft_version")}:${property("create_forge_version")}:slim") {
        isTransitive = false
    }

    // Flywheel API (NeoForge) – gives you the API types used from flywheel in common
    compileOnly("dev.engine-room.flywheel:flywheel-neoforge-api-${property("minecraft_version")}:${property("flywheel_version")}")

    // Catnip (Common) – provides net.createmod.catnip.* classes used across your common sources
    compileOnly("net.createmod.catnip:Catnip-Common-${property("minecraft_version")}:${property("catnip_version")}")

    // Ponder (NeoForge) – provides net.createmod.ponder.* API used by common
    compileOnly("net.createmod.ponder:Ponder-NeoForge-${property("minecraft_version")}:${property("ponder_version")}")

    // javax annotations used in sources (@ParametersAreNonnullByDefault, @Nullable)
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    // (Optional) if you reference JetBrains annotations anywhere
    // compileOnly("org.jetbrains:annotations:24.1.0")
}


tasks.processResources {
    // must be part of primary mod to be findable
    exclude("resourcepacks/")

    // don't add development or to-do files into built jar
    exclude("**/*.bbmodel", "**/*.lnk", "**/*.xcf", "**/*.md", "**/*.txt", "**/*.blend", "**/*.blend1")
}

sourceSets.main {
    resources { // include generated resources in resources
        srcDir("src/generated/resources")
        exclude(".cache/**")
        exclude("assets/create/**")
    }
    blossom.javaSources {
        property("version", "mod_version"())
        property("gitCommit", rootProject.extra["gitHash"].toString())
    }
}

operator fun String.invoke(): String {
    return rootProject.ext[this] as? String
        ?: throw IllegalStateException("Property $this is not defined")
}
