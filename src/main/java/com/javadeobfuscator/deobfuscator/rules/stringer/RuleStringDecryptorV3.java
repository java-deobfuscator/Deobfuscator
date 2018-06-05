/*
 * Copyright 2018 Sam Sun <github-contact@samczsun.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.javadeobfuscator.deobfuscator.rules.stringer;

import com.javadeobfuscator.deobfuscator.Deobfuscator;
import com.javadeobfuscator.deobfuscator.rules.Rule;
import com.javadeobfuscator.deobfuscator.transformers.Transformer;
import com.javadeobfuscator.deobfuscator.transformers.stringer.v9.StringEncryptionTransformer;
import com.javadeobfuscator.deobfuscator.utils.TransformerHelper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.Collections;

public class RuleStringDecryptorV3 implements Rule {
    @Override
    public String getDescription() {
        return "This variant of Stringer's string decryptor makes use of invokedynamic obfuscation within the string decryption classes";
    }

    @Override
    public String test(Deobfuscator deobfuscator) {
        for (ClassNode classNode : deobfuscator.getClasses().values()) {
            for (MethodNode methodNode : classNode.methods) {
                if (!TransformerHelper.basicType(methodNode.desc).equals("(Ljava/lang/Object;III)Ljava/lang/Object;")) {
                    continue;
                }

                boolean isStringer = true;

                isStringer = isStringer && TransformerHelper.containsInvokeStatic(methodNode, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;");
                isStringer = isStringer && TransformerHelper.containsInvokeVirtual(methodNode, "java/lang/Thread", "getStackTrace", "()[Ljava/lang/StackTraceElement;");
                isStringer = isStringer && TransformerHelper.countOccurencesOf(methodNode, IAND) > 20;
                isStringer = isStringer && TransformerHelper.countOccurencesOf(methodNode, IXOR) > 20;
                isStringer = isStringer && TransformerHelper.countOccurencesOf(methodNode, IUSHR) > 10;
                isStringer = isStringer && TransformerHelper.countOccurencesOf(methodNode, ISHL) > 10;

                if (!isStringer) {
                    continue;
                }

                return "Found possible string decryption class " + classNode.name;
            }
        }

        return null;
    }

    @Override
    public Collection<Class<? extends Transformer>> getRecommendTransformers() {
        return Collections.singleton(StringEncryptionTransformer.class);
    }
}
