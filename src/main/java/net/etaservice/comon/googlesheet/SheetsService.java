package net.etaservice.comon.googlesheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.tasks.TasksScopes;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.etaservice.comon.google.GoogleCendentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@PropertySource("application-${spring.profiles.active}.properties")
@EnableConfigurationProperties
@Slf4j
public class SheetsService implements ISheetService {


    @Autowired
    private GoogleCendentials googleCendentials;

    @Value("${sheetapi.spreadsheet.id}")
    private String spreadsheetId;

    private static final String APPLICATION_NAME = "ETASERVICE SHEET API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @SneakyThrows
    @Scheduled(fixedDelay = 3600000)
    public void scheduleFixedDelayTask() {
        log.info("Call Service to refresh token ");
        googleCendentials.getCredentials();
        log.info(" END ---Call Service to refresh token");
    }

    @SneakyThrows
    @Override
    public Sheets service()  {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleCendentials.getCredentials())
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        return service;
    }

    @SneakyThrows
    @Override
    public ValueRange getDataSheet(String range)  {
        return service().spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
    }

    @SneakyThrows
    @Override
    public ValueRange getDataSheetWithFormula(String range)  {
        Sheets.Spreadsheets.Values.Get request =
                service().spreadsheets().values().get(spreadsheetId, range);
        request.setValueRenderOption("FORMULA");
        return request.execute();
    }

    public Sheet getSheetByName(String sheetName) throws GeneralSecurityException, IOException {
        List<Sheet> sheets = service().spreadsheets().get(spreadsheetId).execute().getSheets();
        Sheet sheet = null;
        for (Sheet s : sheets) {
            if (s.getProperties().getTitle().equals(sheetName)) {
                sheet = s;
                break;
            }
        }
        return sheet;
    }


    @SneakyThrows
    @Override
    public UpdateValuesResponse inserData(String range, ValueRange body) {
        return service().spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }

}
