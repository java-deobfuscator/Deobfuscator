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

package com.javadeobfuscator.deobfuscator.executor.values;

import com.javadeobfuscator.deobfuscator.utils.Utils;
import org.objectweb.asm.tree.AbstractInsnNode;

public class JavaAddress extends JavaValue {

    private final AbstractInsnNode value;

    public JavaAddress(AbstractInsnNode value) {
        this.value = value;
    }

    @Override
    public JavaValue copy() {
        return new JavaAddress(value);
    }

    public Object value() {
        return this.value;
    }

    public String toString() {
        return "JavaAddress(addr=" + Utils.prettyprint(this.value) + ")";
    }
}
