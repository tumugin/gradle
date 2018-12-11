/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.groovy.scripts.internal

import org.gradle.groovy.scripts.ScriptSource
import org.gradle.internal.hash.FileHasher
import org.gradle.internal.hash.HashCode
import org.gradle.internal.resource.TextResource
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.junit.Rule
import spock.lang.Specification

class DefaultScriptSourceHasherTest extends Specification {
    @Rule
    TestNameTestDirectoryProvider tmpDir = new TestNameTestDirectoryProvider()
    def hash = HashCode.fromInt(123)

    def fileHasher = Mock(FileHasher)
    def scriptHasher = new DefaultScriptSourceHasher(fileHasher)

    def "hashes underlying file when not cached"() {
        def file = tmpDir.createFile("testfile")
        def resource = Stub(TextResource) {
            isContentCached() >> false
            getFile() >> file
        }
        def script = Stub(ScriptSource) {
            getResource() >> resource
        }

        when:
        def result = scriptHasher.hash(script)

        then:
        result == hash

        and:
        1 * fileHasher.hash(file) >> hash
    }

    def "hashes content when not cached and not a file"() {
        def resource = Stub(TextResource) {
            isContentCached() >> false
            getFile() >> null
            getText() >> "alma"
        }
        def script = Stub(ScriptSource) {
            getResource() >> resource
        }

        when:
        def result = scriptHasher.hash(script)

        then:
        result == HashCode.fromString("6448f0e21a54bd0519552bd538b03fef")

        and:
        0 * fileHasher._
    }

    def "hashes content when cached"() {
        def file = tmpDir.createFile("testfile")
        def resource = Stub(TextResource) {
            isContentCached() >> true
            getFile() >> file
            getText() >> "alma"
        }
        def script = Stub(ScriptSource) {
            getResource() >> resource
        }

        when:
        def result = scriptHasher.hash(script)

        then:
        result == HashCode.fromString("6448f0e21a54bd0519552bd538b03fef")

        and:
        0 * fileHasher._
    }
}
