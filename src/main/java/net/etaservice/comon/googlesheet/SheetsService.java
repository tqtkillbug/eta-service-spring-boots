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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@PropertySource("application-${spring.profiles.active}.properties")
@EnableConfigurationProperties
public class SheetsService implements ISheetService {
    private static final String APPLICATION_NAME = "ETASERVICE SHEET API";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    @Value("${sheetapi.tokens.path}")
    private String TOKENS_DIRECTORY_PATH;

    @Value("${sheetapi.credentials.path}")
    private String CREDENTIALS_FILE_PATH;

    @Value("${sheetapi.spreadsheet.id}")
    private String spreadsheetId;

    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS, TasksScopes.TASKS);


    @Override
    public Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    @SneakyThrows
    @Override
    public Sheets service()  {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
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
