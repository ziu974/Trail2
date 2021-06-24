package com.example.trail.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.example.trail.TrailApplication;
import com.example.trail.database.AppPreferencesHelper;
import com.example.trail.network.helper.NetworkHelper;
import com.example.trail.view.splash.SplashViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.scopes.ActivityScoped;

/** https://github.com/MindorksOpenSource/android-mvvm-architecture */
// Activity 별로 extend해가면 됨
// XML View Binding과 ViewModel을 그 Activity와 연결해줌
public abstract class BaseActivity<T extends ViewDataBinding, V extends BaseViewModel> extends AppCompatActivity implements BaseFragment.Callback {


    NetworkHelper networkHelper;
//    @Inject
    AppPreferencesHelper appPreferencesHelper;

    private T binding;
    private V viewModel;

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract V getViewModel();

    /**
     * Override for observing ViewModel
     *
     * @return void
     */
    public abstract void observeViewModel();

    public T getViewDataBinding() {
        return binding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //(Hilt로 변경, 이 방식은 dagger2 ->)   performDependencyInjection();       // 순서?: before super.onCreate()이어야 함 (공식문서참고)
        performDataBinding();
        // fixme 필요하면,주석해제
//          initBaseActivity();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  // why?
//        observeViewModel();     // 06.03 이렇게 불러야 추가됐었던건가

        super.onCreate(savedInstanceState);
    }

//    private void initBaseActivity() {   // necessary?
//        ((TrailApplication) getApplication()).setCurrentActivity(this);
//        this.viewModel.setNetworkHelper(networkHelper);
//    }

    private void performDataBinding() { // 코드 해석
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        this.viewModel = viewModel == null ? getViewModel() : viewModel;
        binding.setVariable(getBindingVariable(), viewModel);
        binding.setLifecycleOwner(this);    // added: live data 사용을 위해 필요할 것 같아서
        binding.executePendingBindings();
    }

    @Override
    public void onFragmentAttached() { }

    @Override
    public void onFragmentDetached(String tag) { }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
