/*
 * Copyright 2016 Sam Sun <me@samczsun.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.javadeobfuscator.deobfuscator.transformers.general.peephole;

import com.javadeobfuscator.deobfuscator.config.TransformerConfig;
import com.javadeobfuscator.deobfuscator.transformers.Transformer;
import com.javadeobfuscator.deobfuscator.utils.InstructionModifier;
import com.javadeobfuscator.deobfuscator.utils.Utils;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

public class DeadCodeRemover extends Transformer<TransformerConfig> {
    @Override
    public boolean transform() throws Throwable {
        int deadInstructions = 0;
        for (ClassNode classNode : classes.values()) {
            for (MethodNode methodNode : classNode.methods) {
                if (methodNode.instructions.getFirst() == null) continue;

                InstructionModifier modifier = new InstructionModifier();

                try {
                    Frame<BasicValue>[] frames = new Analyzer<>(new BasicInterpreter()).analyze(classNode.name, methodNode);
                    for (int i = 0; i < methodNode.instructions.size(); i++) {
                        if (!Utils.isInstruction(methodNode.instructions.get(i))) continue;
                        if (frames[i] != null) continue;

                        modifier.remove(methodNode.instructions.get(i));
                        deadInstructions++;
                    }
                } catch (Exception x) {
                    logger.error("Error analyzing frames for method ", x);
                    continue;
                }

                modifier.apply(methodNode);

                // empty try catch nodes are illegal
                if (methodNode.tryCatchBlocks != null) {
                    methodNode.tryCatchBlocks.removeIf(tryCatchBlockNode -> Utils.getNext(tryCatchBlockNode.start) == Utils.getNext(tryCatchBlockNode.end));
                }
            }
        }
        logger.info("Removed {} dead instructions", deadInstructions);
        return deadInstructions > 0;
    }
}
