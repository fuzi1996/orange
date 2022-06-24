package com.github.freakchick.orange.node;

import com.github.freakchick.orange.context.Context;

import java.util.Set;


public interface SqlNode {

    boolean apply(Context context);

    boolean applyParameter(Set<String> set);

}
