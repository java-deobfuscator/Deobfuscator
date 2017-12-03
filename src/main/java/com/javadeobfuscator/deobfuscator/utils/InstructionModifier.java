/*
 * Copyright 2017 Sam Sun <github-contact@samczsun.com>
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

package com.javadeobfuscator.deobfuscator.utils;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modifying instructions in a method while iterating it is hard. Here's an easier way to do it
 */
public class InstructionModifier {
    private static final InsnList EMPTY_LIST = new InsnList();

    private final Map<AbstractInsnNode, InsnList> replacements = new HashMap<>();

    public void replace(AbstractInsnNode original, AbstractInsnNode replacement) {
        InsnList singleton = new InsnList();
        singleton.add(replacement);
        replacements.put(original, singleton);
    }

    public void replace(AbstractInsnNode original, InsnList replacements) {
        this.replacements.put(original, replacements);
    }

    public void remove(AbstractInsnNode original) {
        replacements.put(original, EMPTY_LIST);
    }

    public void removeAll(List<AbstractInsnNode> toRemove) {
        for (AbstractInsnNode insn : toRemove) {
            remove(insn);
        }
    }

    public void apply(MethodNode methodNode) {
        replacements.forEach((insn, list) -> {
            methodNode.instructions.insert(insn, list);
            methodNode.instructions.remove(insn);
        });
    }
}
