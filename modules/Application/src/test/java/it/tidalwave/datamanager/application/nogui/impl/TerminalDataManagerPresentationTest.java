package it.tidalwave.datamanager.application.nogui.impl;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import it.tidalwave.role.SimpleComposite;
import it.tidalwave.role.ui.Displayable;
import it.tidalwave.role.ui.PresentationModel;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/***********************************************************************************************************************
 *
 **********************************************************************************************************************/
@Slf4j
public class TerminalDataManagerPresentationTest
  {
    private TerminalDataManagerPresentation underTest;

    private List<String> output;

    /******************************************************************************************************************/
    @BeforeMethod
    public void setup()
      {
        output = new ArrayList<>();
        underTest = new TerminalDataManagerPresentation(output::add);
      }

    /******************************************************************************************************************/
    @Test
    public void test_renderManagedFiles()
      {
        // given
        final var p1 = createPm("/foo/bar/1");
        final var p2 = createPm("/foo/bar/2", "2:f1", "2:f2");
        final var p3 = createPm("/foo/bar/3", "3:f1", "3:f2", "3:f3");
        final var pm = PresentationModel.of("", SimpleComposite.ofCloned(List.of(p1, p2, p3)));
        // when
        underTest.renderManagedFiles(pm);
        // then
        assertThat(output, is(List.of(
                "00001) /foo/bar/1",
                "00002) /foo/bar/2",
                "    2:f1",
                "    2:f2",
                "00003) /foo/bar/3",
                "    3:f1",
                "    3:f2",
                "    3:f3")));
      }

    /******************************************************************************************************************/
    @Test
    public void test_renderBackups()
      {
        // given
        final var p1 = createPm("Backup 1");
        final var p2 = createPm("Backup 2", "/foo/bar/1", "/foo/bar/2");
        final var p3 = createPm("Backup 3", "/foo/bar/3", "/foo/bar/4", "/foo/bar/5");
        final var pm = PresentationModel.of("", SimpleComposite.ofCloned(List.of(p1, p2, p3)));
        // when
        underTest.renderBackups(pm);
        // then
        assertThat(output, is(List.of(
                "00001) Backup 1",
                "00002) Backup 2",
                "    /foo/bar/1",
                "    /foo/bar/2",
                "00003) Backup 3",
                "    /foo/bar/3",
                "    /foo/bar/4",
                "    /foo/bar/5")));
      }


    /******************************************************************************************************************/
    @Nonnull
    private static PresentationModel createPm (@Nonnull final String display, @Nonnull final String ... strings)
      {
        final var fp = Stream.of(strings)
                             .map(v -> PresentationModel.of("",  Displayable.of(v)))
                             .toList();
        return PresentationModel.of("", List.of(Displayable.of(display), SimpleComposite.ofCloned(fp)));
      }
  }
