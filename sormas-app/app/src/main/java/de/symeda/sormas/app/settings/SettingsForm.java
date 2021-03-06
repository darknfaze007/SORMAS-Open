package de.symeda.sormas.app.settings;

import android.accounts.AuthenticatorException;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.net.ConnectException;

import de.symeda.sormas.app.AbstractRootTabActivity;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.databinding.SettingsFragmentLayoutBinding;
import de.symeda.sormas.app.rest.RetroProvider;
import de.symeda.sormas.app.rest.SynchronizeDataAsync;
import de.symeda.sormas.app.util.FormTab;
import de.symeda.sormas.app.util.SyncCallback;

/**
 * Created by Stefan Szczesny on 27.07.2016.
 */
public class SettingsForm extends FormTab {

    private SettingsFragmentLayoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment_layout, container, false);

        binding.configServerUrl.setValue((String)ConfigProvider.getServerRestUrl());

        binding.configDropData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dropData();
            }
        });
        binding.configDropData.setVisibility(View.GONE);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean hasUser = ConfigProvider.getUser() != null;
        binding.configChangePIN.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.configSyncLog.setVisibility(hasUser ? View.VISIBLE : View.GONE);
        binding.configLogout.setVisibility(hasUser ? View.VISIBLE : View.GONE);
    }

    /**
     * Only possible when server connection is available
     */
    private void dropData() {

        if (!RetroProvider.isConnected()) {
            try {
                RetroProvider.connect(getContext());
            } catch (AuthenticatorException e) {
                Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (RetroProvider.ApiVersionException e) {
                Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            } catch (ConnectException e) {
                Snackbar.make(getActivity().findViewById(R.id.base_layout), e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }

        if (RetroProvider.isConnected()) {
            binding.configProgressBar.setVisibility(View.VISIBLE);

            DatabaseHelper.clearTables(true);
            SynchronizeDataAsync.call(SynchronizeDataAsync.SyncMode.ChangesAndInfrastructure, getContext(), new SyncCallback() {
                @Override
                public void call(boolean syncFailed) {
                    SettingsForm.this.onResume();
                    binding.configProgressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Snackbar.make(getActivity().findViewById(R.id.base_layout), R.string.snackbar_no_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    public String getServerUrl() {
        return binding.configServerUrl.getValue();
    }

    @Override
    public AbstractDomainObject getData() {
        return null;
    }
}