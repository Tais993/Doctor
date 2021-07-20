package de.ialistannen.docfork.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.ialistannen.javadocapi.model.JavadocElement;
import de.ialistannen.javadocapi.model.QualifiedName;
import de.ialistannen.javadocapi.model.comment.JavadocComment;
import de.ialistannen.javadocapi.model.types.JavadocMethod;
import de.ialistannen.javadocapi.model.types.JavadocType;
import de.ialistannen.javadocapi.model.types.PossiblyGenericType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DeclarationFormatterTest {

  private DeclarationFormatter formatter;

  @BeforeEach
  void setUp() {
    formatter = new DeclarationFormatter(50);
  }

  @Test
  void annotationFormatting() {
    String text = """
        @Deprecated(value = "Hello, this is deprecated for removal my friend")""";

    assertEquals(
        """
            @Deprecated(
              value = "Hello, this is deprecated for removal my friend"
            )""",
        formatter.formatDeclaration(new JavadocElement() {
          @Override
          public QualifiedName getQualifiedName() {
            return null;
          }

          @Override
          public Optional<JavadocComment> getComment() {
            return Optional.empty();
          }

          @Override
          public String getDeclaration(DeclarationStyle declarationStyle) {
            return text;
          }
        })
    );
  }

  @Test
  void testMethodParametersChop() {
    String text = """
        @Deprecated(value = "Hello, this is deprecated for removal my friend")
        public static <T> T foobar(String a, int b, int helloIAmLong, @Nonnull String andIMightNotBeNull)""";

    assertEquals(
        """
            @Deprecated(
              value = "Hello, this is deprecated for removal my friend"
            )
            public static <T> T foobar(
              String a,
              int b,
              int helloIAmLong,
              @Nonnull String andIMightNotBeNull
            )""",
        formatter.formatDeclaration(new FakeMethod(text))
    );
  }

  @Test
  void testMethodParametersNoChop() {
    String text = """
        @Deprecated(value = "Hello, this is deprecated for removal my friend")
        public static <T> T foobar(String a, int b)""";

    assertEquals(
        """
            @Deprecated(
              value = "Hello, this is deprecated for removal my friend"
            )
            public static <T> T foobar(String a, int b)""",
        formatter.formatDeclaration(new FakeMethod(text))
    );
  }

  @Test
  void testMethodParametersGeneric() {
    String text = """
        public static void foobar(Map<String, Integer> thisIsMap, Map<List<String>, Map<Integer, ? extends String>> foo)""";

    assertEquals(
        """
            public static void foobar(
              Map<String, Integer> thisIsMap,
              Map<List<String>, Map<Integer, ? extends String>> foo
            )""",
        formatter.formatDeclaration(new FakeMethod(text))
    );
  }

  @Test
  void testTypeChop() {
    String text = """
        @Deprecated(value = "Hello, this is deprecated for removal my friend")
        public static class FooBar<T> extends SomeVeryLongClass implements A, B, C, D, MoreClasses""";

    assertEquals(
        """
            @Deprecated(
              value = "Hello, this is deprecated for removal my friend"
            )
            public static class FooBar<T>
              extends SomeVeryLongClass
              implements A, B, C, D, MoreClasses""",
        formatter.formatDeclaration(new FakeType(
            text, new QualifiedName(""), List.of(new QualifiedName(""))
        ))
    );
  }

  @Test
  void testTypeChopGeneric() {
    String text = """
        public static class FooBar<T> extends String<Int, Int> implements A<Map<Int, String>>, B<List<StringBuilder>>""";

    assertEquals(
        """
            public static class FooBar<T>
              extends String<Int, Int>
              implements A<Map<Int, String>>,
                         B<List<StringBuilder>>""",
        formatter.formatDeclaration(new FakeType(
            text, new QualifiedName(""), List.of(new QualifiedName(""))
        ))
    );
  }

  @Test
  void testTypeJFrame() {
    String text = """
        @JavaBean(defaultProperty = "JMenuBar", description = "A toplevel window which can be minimized to an icon.")
        public class JFrame extends Frame implements WindowConstants, Accessible, RootPaneContainer, TransferHandler.HasGetTransferHandler""";

    assertEquals(
        """
            @JavaBean(
              defaultProperty = "JMenuBar",
              description = "A toplevel window which can be minimized to an icon."
            )
            public class JFrame
              extends Frame
              implements WindowConstants,
                         Accessible,
                         RootPaneContainer,
                         TransferHandler.HasGetTransferHandler""",
        formatter.formatDeclaration(new FakeType(
            text, new QualifiedName(""), List.of(new QualifiedName(""))
        ))
    );
  }

  @Test
  void testTypeNoChop() {
    String text = """
        @Deprecated(value = "Hello, this is deprecated for removal my friend")
        public static class Foo<T> extends A implements B""";

    assertEquals(
        """
            @Deprecated(
              value = "Hello, this is deprecated for removal my friend"
            )
            public static class Foo<T> extends A implements B""",
        formatter.formatDeclaration(new FakeType(
            text, new QualifiedName(""), List.of(new QualifiedName(""))
        ))
    );
  }

  private static class FakeMethod extends JavadocMethod {

    private final String declaration;

    public FakeMethod(String declaration) {
      super(null, null, List.of(), List.of(), List.of(), List.of(), List.of(), null);
      this.declaration = declaration;
    }

    @Override
    public String getDeclaration(DeclarationStyle style) {
      return declaration;
    }
  }

  private static class FakeType extends JavadocType {

    private final String declaration;

    public FakeType(String declaration, QualifiedName superclass, List<QualifiedName> interfaces) {
      super(
          null,
          List.of(),
          List.of(),
          null,
          List.of(),
          List.of(),
          null,
          interfaces.stream()
              .map(it -> new PossiblyGenericType(it, List.of()))
              .collect(Collectors.toList()),
          new PossiblyGenericType(superclass, List.of())
      );
      this.declaration = declaration;
    }

    @Override
    public String getDeclaration(DeclarationStyle style) {
      return declaration;
    }
  }

}
