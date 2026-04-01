/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.types.DateParser;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_import.beans.Accrual_schedule_type;
import org.softcaster.easy_import.beans.Accrual_schedule_typeDAO;
import org.softcaster.easy_import.beans.Bond_registry;
import org.softcaster.easy_import.beans.Bond_registryDAO;
import org.softcaster.easy_import.beans.Country;
import org.softcaster.easy_import.beans.CountryDAO;
import org.softcaster.easy_import.beans.Currency;
import org.softcaster.easy_import.beans.CurrencyDAO;
import org.softcaster.easy_import.beans.Daycount;
import org.softcaster.easy_import.beans.DaycountDAO;
import org.softcaster.easy_import.beans.Form;
import org.softcaster.easy_import.beans.FormDAO;
import org.softcaster.easy_import.beans.Frequency;
import org.softcaster.easy_import.beans.FrequencyDAO;
import org.softcaster.easy_import.beans.Issuer;
import org.softcaster.easy_import.beans.IssuerDAO;
import org.softcaster.easy_import.beans.JsonBond;
import org.softcaster.easy_import.beans.Roll_convention;
import org.softcaster.easy_import.beans.Roll_conventionDAO;
import org.softcaster.easy_import.beans.Type_of_interest;
import org.softcaster.easy_import.beans.Type_of_interestDAO;
import org.softcaster.easy_import.xml.BondLoaderMgr;
import org.softcaster.easy_import.xml.ItemBond;

/**
 *
 * @author ep
 */
public class BondImportMgr implements IImportMgr {

    private static BondImportMgr _instance = null;

    // Stringa,in formato JSON,  tornata dalla Banca d'Italia
    private String response = "";
    private final String URL_BI = "https://nna.bancaditalia.it/nna/dettaglioTitolo.do?evento=dettaglio&codTit=";
    private final String URL_STI = "https://www.simpletoolsforinvestors.eu/data/definitions/";

    private HttpURLConnection connection = null;
    private BondLoaderMgr loader = null;

    // DAO
    private CurrencyDAO currencyDAO = null;
    private CountryDAO countryDAO = null;
    private IssuerDAO issuerDAO = null;
    private Type_of_interestDAO toiDAO = null;
    private FormDAO formDAO = null;
    private DaycountDAO daycountDAO = null;
    private Roll_conventionDAO rollConvDAO = null;
    private Accrual_schedule_typeDAO astDAO = null;
    private FrequencyDAO frequencyDAO = null;
    private Bond_registryDAO bondRegistryDAO = null;

    // Bean
    private Currency currency = null;
    private Country country = null;
    private Issuer issuer = null;
    private Type_of_interest toi = null;
    private Form form = null;
    private Daycount daycount = null;
    private Roll_convention rollConv = null;
    private Accrual_schedule_type ast = null;
    private Frequency frequency = null;

    private void createDAOs() {
        currencyDAO = new CurrencyDAO();
        countryDAO = new CountryDAO();
        issuerDAO = new IssuerDAO();
        toiDAO = new Type_of_interestDAO();
        formDAO = new FormDAO();
        daycountDAO = new DaycountDAO();
        rollConvDAO = new Roll_conventionDAO();
        astDAO = new Accrual_schedule_typeDAO();
        frequencyDAO = new FrequencyDAO();
        bondRegistryDAO = new Bond_registryDAO();
    }

    private void createBeans() {
        currency = new Currency();
        country = new Country();
        issuer = new Issuer();
        toi = new Type_of_interest();
        form = new Form();
        daycount = new Daycount();
        rollConv = new Roll_convention();
        ast = new Accrual_schedule_type();
        frequency = new Frequency();
    }

    private BondImportMgr() {

        if (loader == null) {
            try {
                loader = BondLoaderMgr.getInstance();
                createDAOs();
                createBeans();
            } catch (FileNotFoundException | XMLStreamException ex) {
                String error = "Error creating ImportMgr: " + " [" + ex.getLocalizedMessage() + "]";
                LoggerMgr.logError(error);
                loader = null;
            }
        }
    }

    private HttpURLConnection getConnection(String _url) throws MalformedURLException, IOException {
        if (connection == null) {
            URL url = new URL(_url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml, application/json");
        }

        return connection;
    }

    private void resetConnection() {
        connection = null;
    }

    // Importa da isin code
    private void importIsinCode() throws IOException {
        String importPath = System.getProperty("user.dir") + "/import";
        String fileName = Paths.get(importPath + "/bonds_ita.txt").toString();

        String data = readFileFromFileSystem(fileName);
        String tokens = "[";
        StringTokenizer tokenizer = new StringTokenizer(data, tokens);

        while (tokenizer.hasMoreTokens()) {
            BondInfo info = parseLine(tokenizer.nextToken());
            if (info != null) {
                loadXml(info);
            }
            //System.out.println(info.code + "," + info.isin);
        }
    }

    String readFileFromFileSystem(String filePath) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            String error = "Error reading file: " + filePath + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
            return null;
        }
    }

    private BondInfo parseLine(String line) {
        String tokens = ",[\\\"";
        StringTokenizer tokenizer = new StringTokenizer(line, tokens);
        int cnt = 0;
        BondInfo info = new BondInfo();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (cnt) {
                case 0 -> {
                    info.code = Integer.parseInt(token);
                    cnt++;
                }
                case 1 -> {
                    info.isin = token;
                    cnt++;
                }
                default -> {
                    return info;
                }
            }
        }
        return null;
    }

    private void loadXml(BondInfo info) throws IOException {
        String url = URL_STI + info.code + ".xml";
        HttpURLConnection conn = getConnection(url);
        //System.out.println(conn.getResponseCode());
        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())))) {
            Stream<String> lines = br.lines();
            lines.forEach(System.out::println);
            //String response = br.readLine();
        }
        resetConnection();
    }

    // Allinea response , in formato JSON, relativo al bond specificato dall'isin
    private void getRawData(String isin) throws IOException {
        String url = URL_BI + isin;
        HttpURLConnection conn = getConnection(url);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())))) {
            Stream<String> lines = br.lines();
            // Reset ultima richiesta
            response = "";
            // Impacca tutte le linee
            Consumer<String> addElement = s -> {
                response = s;
            };
            lines.forEach(addElement);
            // Chiusura buffer
            br.close();
        }

        // Reset connessione
        resetConnection();
    }

    private JsonBond getBondDataFromBI(String isin) throws IOException {

        // Richiesta dato ISIN
        getRawData(isin);

        // Converte da JSON a Pojo
        ObjectMapper om = new ObjectMapper();
        return om.readValue(response, JsonBond.class);
    }

    private List<String> getIsinList() throws FileNotFoundException, XMLStreamException {
        if (loader != null) {
            return loader.getIsinList();
        } else {
            return null;
        }
    }

    private ItemBond getBondDataFromXML(String isin) throws IOException, FileNotFoundException, XMLStreamException {

        if (loader != null) {
            return loader.getBondByIsin(isin);
        } else {
            return null;
        }
    }

    /*
    {"codTit":"IT0005668238","dataChiu":"","datInVal":"02/09/2025","datFiVal":"31/12/2099","emitTit":"00000000001",
    "desTit":"ITALIA/4.65 BTP 20551001","valPar":"242","datSca":"01/10/2055","codCfi":"DBFTFB","datEmi":"09/09/2025",
    "tipQuota":"B","valNom":"1000","staEmiTit":"86","tipoTasso":"0","periodCed":"1","tassoE":"4,65","perCed":",27951",
    "datPrCed":"01/10/2025","codVeCom":"020","codFisn":"ITALIA/4.65 BTP 20551001","denomSocEstesa":"REPUBBLICA ITALIANA",
    "codLei":"815600DE60799F5A9309","comparto":"BUONI DEL TESORO POLIENNALI","descrStaEmiTit":"ITALIA",
    "descrValPar":"EURO  UNIONE ECONOMICA E MONETARIA",
    "desTitCompleta":"ITALIA/4.65 BTP 20551001 - BUONI DEL TESORO POLIENNALI","decodificaTipoTasso":"FIXED RATE ",
    "decodificaPeriodicitaCedola":null,"decodificaFormaTitolo":"BEARER"}
     */
    private boolean saveRecord(ItemBond bond, JsonBond jBond) {
        try {
            currency.setIso_code(bond.currency);
            currencyDAO.loadByIsoCode(currency);

            String alfa3code = jBond.descrStaEmiTit.substring(0, 3);
            country.setAlfa_3_code(alfa3code);
            countryDAO.loadByAlfa3Code(country);

            issuer.setShort_issuer_name("REP ITA");
            issuerDAO.loadByCode(issuer);

            toi.setCode("FIXED"/*jBond.decodificaTipoTasso*/);
            toiDAO.loadByIdx(toi);

            form.setCode("BEARER"/*jBond.decodificaFormaTitolo*/);
            formDAO.loadByIdx(form);

            frequency.setCode("SEMI-ANNUAL");
            frequencyDAO.loadByIdx(frequency);

            daycount.setCode("ACT_ACT");
            daycountDAO.loadByIdx(daycount);

            rollConv.setCode("NONE");
            rollConvDAO.loadByIdx(rollConv);

            ast.setCode("NONE");
            astDAO.loadByIdx(ast);

            Bond_registry bondRegistry = new Bond_registry();
            bondRegistry.setAccrual_schedule_type(ast.getId_accrual_schedule_type());
            bondRegistry.setBusiness_days(2);
            bondRegistry.setCalendar(country.getCalendar());
            bondRegistry.setCfi_code(jBond.codCfi);
            bondRegistry.setCoupon_frequency(frequency.getId_frequency());
            bondRegistry.setCurrency(currency.getId_currency());
            bondRegistry.setDaycount(daycount.getId_daycount());
            DateParser parser = new DateParser(jBond.datPrCed);
            Date dt = new Date(parser.year(), parser.month(), parser.day());
            bondRegistry.setFirst_coupon_payment_date(dt.sqlDate());
            String firstCedStr = jBond.perCed;
            double firstCed=Double.parseDouble(firstCedStr.replaceAll(",", "."));
            bondRegistry.setFirst_coupon_rate(firstCed);
            bondRegistry.setFisn(jBond.codFisn);
            bondRegistry.setForm(form.getId_form());
            bondRegistry.setInterest_rate(bond.couponrate);
            bondRegistry.setIsin(bond.isincode);
            bondRegistry.setIssue_date(bond.issuedate);
            bondRegistry.setIssue_description(jBond.desTitCompleta);
            bondRegistry.setIssue_price(bond.issueprice);
            bondRegistry.setIssuer(issuer.getId_issuer());
            bondRegistry.setLei(jBond.codLei);
            bondRegistry.setMaturity_date(bond.redemptiondate);
            bondRegistry.setNominal_value(bond.amount);
            bondRegistry.setRedempion_price(bond.redemptionprice);
            bondRegistry.setRoll_convention(rollConv.getId_roll_convention());
            bondRegistry.setType_of_interest(toi.getId_type_of_interest());

            bondRegistryDAO.insertOrUpdate(bondRegistry);
            return true;
        } catch (Exception ex) {
            String error = "Error importing Bond: " + bond.isincode + " function saveRecord - error: [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
            return false;
        }
    }

    @Override
    public void start(IProgressInfo progressInfo) {

        String isin = "";
        try {
            // Lista Bond 
            List<String> items = getIsinList();
            int progress = items.size() / 10;
            progress = 100 / progress;
            int cnt = 1;
            for (String item : items) {
                isin = item;

                // Carica dati bond dato isin
                ItemBond bond = getBondDataFromXML(isin);
                JsonBond jBond = getBondDataFromBI(isin);

                // Salva il record
                saveRecord(bond, jBond);
                if (progressInfo != null) {
                    progressInfo.setProgress(progress + cnt);
                    cnt++;
                }
            }

        } catch (FileNotFoundException | XMLStreamException ex) {
            String error = "Error importing Bond: " + isin + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        } catch (IOException ex) {
            String error = "Error importing Bond: " + isin + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        } finally {
            terminate();
        }

    }

    @Override
    public void terminate() {
        if (currencyDAO != null) {
            currencyDAO.closeStatements();
        }

        if (countryDAO != null) {
            countryDAO.closeStatements();
        }

        if (formDAO != null) {
            formDAO.closeStatements();
        }

        if (daycountDAO != null) {
            daycountDAO.closeStatements();
        }

        if (rollConvDAO != null) {
            rollConvDAO.closeStatements();
        }

        if (astDAO != null) {
            astDAO.closeStatements();
        }

        if (frequencyDAO != null) {
            frequencyDAO.closeStatements();
        }

        if (toiDAO != null) {
            toiDAO.closeStatements();
        }

        if (issuerDAO != null) {
            issuerDAO.closeStatements();
        }

        if (bondRegistryDAO != null) {
            bondRegistryDAO.closeStatements();
        }
        
        LoggerMgr.logInfo("Import terminated");
    }

    public static BondImportMgr getInstance() {
        if (_instance == null) {
            _instance = new BondImportMgr();
        }
        return _instance;
    }
}
//www.borsaitaliana.it/obbligazioni/it0005273013.pdf
//www.borsaitaliana.it/obbligazioni/it0005634800.pdf
// https://live.euronext.com/en/ajax/getFactsheetInfoBlock/BONDS/IT0005634800-MOTX/fs_instrumentinfo_block
// https://live.euronext.com/en/ajax/getFactsheetInfoBlock/BONDS/IT0005634800-MOTX/fs_couponinfo_block