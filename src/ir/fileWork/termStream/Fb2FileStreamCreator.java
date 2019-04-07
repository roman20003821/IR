package ir.fileWork.termStream;

import com.kursx.parser.fb2.Body;
import com.kursx.parser.fb2.FictionBook;
import com.kursx.parser.fb2.Person;
import ir.tools.Pair;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Fb2FileStreamCreator implements StreamCreator {

    private Analyser analyser;

    public Fb2FileStreamCreator(Analyser analyser) {
        this.analyser = analyser;
    }

    @Override
    public Stream<Pair<StreamEntity, Integer>> termAndDocIdStream(String path, int docId) throws IOException {
        List<Fb2StreamEntity> res = new LinkedList<>();
        try {
            FictionBook fb2 = new FictionBook(new File(path));
            addTermsFromAuthors(res, fb2.getAuthors(), "author");
            addTermsFromBody(res, fb2.getBody(), "body");
        } catch (ParserConfigurationException|SAXException e) {
            e.printStackTrace();
            throw new IOException("Bad file: " + path);
        }
        return res.stream().map(fb2StreamEntity -> new Pair<>(fb2StreamEntity, docId));
    }

    private void addTermsFromAuthors(List<Fb2StreamEntity> resList, List<Person> personList, String zone) {
        personList.forEach(it -> {
            analyser.analyse(it.getFullName()).forEach(term -> resList.add(new Fb2StreamEntity(term, zone)));
        });
    }

    private void addTermsFromBody(List<Fb2StreamEntity> resList, Body body, String zone) {
        body.getSections().forEach(section -> {
            section.getElements().forEach(element -> {
                analyser.analyse(element.getText()).forEach(term -> resList.add(new Fb2StreamEntity(term, zone)));
            });
        });
    }

    public static class Fb2StreamEntity extends StreamEntity{
        private String zone;

        public Fb2StreamEntity(String term, String zone) {
            super(term);
            this.zone = zone;
        }

        public String getTerm() {
            return term;
        }

        public String getZone() {
            return zone;
        }
    }
}
