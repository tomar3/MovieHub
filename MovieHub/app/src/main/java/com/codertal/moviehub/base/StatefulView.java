package com.codertal.moviehub.base;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface StatefulView<S extends BaseState> {

    void writeToBundle(Bundle outState, S state);

    S readFromBundle(@NonNull Bundle savedInstanceState);

}
