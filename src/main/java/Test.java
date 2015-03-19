import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElementWrapper;
import org.apache.abdera.parser.stax.FOMDiv;
import org.apache.abdera.util.AbstractExtensionFactory;

import javax.xml.namespace.QName;
import java.util.function.Supplier;

public class Test {

    public static final QName qname = new QName("http://foo/types", "value", "foo");
    private static final Factory extFactory = Abdera.getNewFactory().registerExtension(new ExtFact());

    public static void main(String... args) {
        new Test().run(Abdera.getInstance().getFactory().newEntry());
        waitABit();
        System.out.println("Now, with correct factory");
        new Test().run(extFactory.newEntry());
    }

    private static void waitABit() {
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run(Entry entry) {
        final Content content = entry.getFactory().newContent(Content.Type.XHTML);
        Element el = content.setValue("<p>hello world</p>").getValueElement();

        Value value = extFactory.newExtensionElement(qname);

        value.addExtension(el);
        entry.addExtension(value);

        assert ("entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:foo=\"http://foo/types\"><foo:value><div " +
                "xmlns=\"http://www.w3.org/1999/xhtml\"><p>hello world</p></div></foo:value></entry>")
                .equals(entry.toString());

        doesThisWork(() -> entry.getExtensions(qname).size());
        doesThisWork(() -> entry.<Value>getExtension(qname));
        doesThisWork(() -> entry.getExtension(Value.class).getExtension(FOMDiv.class));
        doesThisWork(() -> entry.<Value>getExtension(qname).getExtension(FOMDiv.class));
        doesThisWork(() -> entry.getExtension(Value.class).getExtension(FOMDiv.class).getValue());
        doesThisWork(() -> entry.<Value>getExtension(qname).getExtension(FOMDiv.class).getValue());

    }

    private <T> void doesThisWork(Supplier<T> supp) {
        try {
            System.out.println("yes: " + supp.get());
        } catch (Throwable t) {
            System.err.println("nope (" + t.getMessage() + ")");
        }
    }

    public static class Value extends ExtensibleElementWrapper {

        public Value(Element internal) {
            super(internal);
        }

        public Value(Factory factory) {
            super(factory, qname);
        }

    }

    public static class ExtFact extends AbstractExtensionFactory {

        public ExtFact() {
            this.addImpl(qname, Value.class);
        }
    }
}
