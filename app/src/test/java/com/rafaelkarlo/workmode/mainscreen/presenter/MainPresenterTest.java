package com.rafaelkarlo.workmode.mainscreen.presenter;

import com.rafaelkarlo.workmode.mainscreen.view.MainView;
import com.rafaelkarlo.workmode.mainscreen.service.WorkModeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPresenterTest {

    @Mock
    private MainView mainView;

    @Mock
    private WorkModeService workModeService;

    private MainPresenter mainPresenter;

    @Before
    public void setupPresenter() {
        mainPresenter = new MainPresenterImpl(workModeService);
        mainPresenter.attachView(mainView);
    }

    @Test
    public void shouldUpdateStatusWhenWorkModeHasBeenActivated() {
        mainPresenter.activateWorkMode();

        verify(workModeService).activate();
        verify(mainView).onWorkModeActivation();
    }

    @Test
    public void shouldUpdateStatusWhenWorkModeHasBeenDeactivated() {
        mainPresenter.deactivateWorkMode();

        verify(workModeService).deactivate();
        verify(mainView).onWorkModeDeactivation();
    }

    @Test
    public void shouldSetViewToActivatedStatusWhenStartingApp() {
        when(workModeService.isActivated()).thenReturn(true);

        mainPresenter.onCreate();

        verify(mainView).onWorkModeActivation();
        verifyNoMoreInteractions(mainView);
    }

    @Test
    public void shouldSetViewToDeactivatedStatusWhenStartingApp() {
        when(workModeService.isActivated()).thenReturn(false);

        mainPresenter.onCreate();

        verify(mainView).onWorkModeDeactivation();
        verifyNoMoreInteractions(mainView);
    }

}