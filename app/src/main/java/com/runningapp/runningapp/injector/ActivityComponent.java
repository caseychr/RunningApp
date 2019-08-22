package com.runningapp.runningapp.injector;

import com.runningapp.runningapp.MapFragment;
import com.runningapp.runningapp.MyRunsFragment;

import dagger.Component;

@Component(modules = {ContextModule.class})
public interface ActivityComponent {

    void inject(MapFragment mapFragment);

    void inject(MyRunsFragment myRunsFragment);
}
