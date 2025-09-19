package accessories.plugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import freemind.extensions.ExportHook;
import freemind.main.Tools;

public class ExportAsMI extends ExportHook {

    public ExportAsMI() {
        super();
    }

    @Override
    public void startupMapHook() {
        if (getController().getMap() == null) {
            return; // No map is open.
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(getTranslatableResourceString("export_as_mi.text"));

        File currentMapFile = getController().getMap().getFile();
        if (currentMapFile != null) {
            String suggestedName = Tools.removeExtension(currentMapFile.getName()) + ".mi";
            chooser.setSelectedFile(new File(currentMapFile.getParent(), suggestedName));
        } else {
            chooser.setSelectedFile(new File("mymap.mi"));
        }

        int result = chooser.showSaveDialog(getController().getView());
        if (result == JFileChooser.APPROVE_OPTION) {
            File saveFile = chooser.getSelectedFile();
            if (saveFile == null) {
                return;
            }

            try {
                String xsltFileName = "accessories/exportMI.xsl";
                boolean success = transformMapWithXslt(xsltFileName, saveFile, "");

                if (success) {
                    // Post-processing step to replace placeholder characters
                    try {
                        String content = new String(Files.readAllBytes(Paths.get(saveFile.toURI())), StandardCharsets.UTF_8);
                        content = content.replace("§", " ");
                        Files.write(Paths.get(saveFile.toURI()), content.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        freemind.main.Resources.getInstance().logException(e);
                        JOptionPane.showMessageDialog(getController().getView(),
                                "Error during post-processing: " + e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(getController().getView(),
                            getResourceString("error_applying_template"), "Freemind",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                freemind.main.Resources.getInstance().logException(e);
                JOptionPane.showMessageDialog(getController().getView(),
                        e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean transformMapWithXslt(String xsltFileName, File saveFile, String areaCode) throws IOException {
        StringWriter writer = getMapXml();
        StringReader reader = new StringReader(writer.getBuffer().toString());
        URL xsltUrl = getResource(xsltFileName);
        if (xsltUrl == null) {
            logger.severe("Can't find " + xsltFileName + " as resource.");
            throw new IllegalArgumentException("Can't find " + xsltFileName + " as resource.");
        }
        InputStream xsltFile = xsltUrl.openStream();
        return transform(new StreamSource(reader), xsltFile, saveFile, areaCode);
    }

    private StringWriter getMapXml() throws IOException {
        StringWriter writer = new StringWriter();
        getController().getMap().getFilteredXml(writer);
        return writer;
    }

    private boolean transform(Source xmlSource, InputStream xsltStream, File resultFile, String areaCode) throws FileNotFoundException {
        Source xsltSource = new StreamSource(xsltStream);
        Result result = new StreamResult(new FileOutputStream(resultFile));
        try {
            TransformerFactory transFact = TransformerFactory.newInstance();
            Transformer trans = transFact.newTransformer(xsltSource);
            trans.setParameter("destination_dir", Tools.fileToRelativeUrlString(new File(resultFile.getAbsolutePath() + "_files/"), resultFile) + "/");
            trans.setParameter("area_code", areaCode);
            trans.setParameter("folding_type", getController().getFrame().getProperty("html_export_folding"));
            trans.transform(xmlSource, result);
        } catch (Exception e) {
            freemind.main.Resources.getInstance().logException(e);
            return false;
        }
        return true;
    }
}
