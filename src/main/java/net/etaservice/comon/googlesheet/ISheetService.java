package net.etaservice.comon.googlesheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;


@Service
public interface ISheetService {
    Credential getCredentials(NetHttpTransport HTTP_TRANSPORT)
            throws IOException;

    Sheets service() throws GeneralSecurityException, IOException;

    ValueRange getDataSheet(String range);

    ValueRange getDataSheetWithFormula(String range) ;

    UpdateValuesResponse inserData(String range, ValueRange body) ;
}
