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

package com.javadeobfuscator.deobfuscator.rules.dasho;

import com.javadeobfuscator.deobfuscator.Deobfuscator;
import com.javadeobfuscator.deobfuscator.rules.Rule;
import com.javadeobfuscator.deobfuscator.transformers.Transformer;
import com.javadeobfuscator.deobfuscator.transformers.dasho.string.StringEncryptionTransformer;
import com.javadeobfuscator.deobfuscator.utils.TransformerHelper;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.Collections;

public class RuleStringDecryptor implements Rule {
    @Override
    public String getDescription() {
        return "Dash-O string decryption can be identified by a function whose name belongs to a method within the JDK. " +
                "It takes at least one int and one string parameter.";
    }

    @Override
    public String test(Deobfuscator deobfuscator) {
        for (ClassNode classNode : deobfuscator.getClasses().values()) {
            for (MethodNode methodNode : classNode.methods) {
                boolean isDashO = true;

                Type[] argTypes = Type.getArgumentTypes(methodNode.desc);

                isDashO = isDashO && TransformerHelper.hasArgumentTypes(argTypes, Type.INT_TYPE, Type.getObjectType("java/lang/String"));
                isDashO = isDashO && TransformerHelper.containsInvokeVirtual(methodNode, "java/lang/String", "toCharArray", "()[C");
                isDashO = isDashO && TransformerHelper.containsInvokeVirtual(methodNode, "java/lang/String", "intern", "()Ljava/lang/String;");
                isDashO = isDashO && TransformerHelper.countOccurencesOf(methodNode, IAND) > 0;
                isDashO = isDashO && TransformerHelper.countOccurencesOf(methodNode, IXOR) > 0;

                if (!isDashO) {
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
