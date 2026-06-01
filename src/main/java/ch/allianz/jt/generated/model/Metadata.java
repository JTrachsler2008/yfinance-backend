package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Metadata for a news article.
 */

@Schema(name = "Metadata", description = "Metadata for a news article.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class Metadata {

  private @Nullable Boolean editorsPick = null;

  public Metadata editorsPick(@Nullable Boolean editorsPick) {
    this.editorsPick = editorsPick;
    return this;
  }

  /**
   * Get editorsPick
   * @return editorsPick
   */
  
  @Schema(name = "editorsPick", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("editorsPick")
  public @Nullable Boolean getEditorsPick() {
    return editorsPick;
  }

  public void setEditorsPick(@Nullable Boolean editorsPick) {
    this.editorsPick = editorsPick;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Metadata metadata = (Metadata) o;
    return Objects.equals(this.editorsPick, metadata.editorsPick);
  }

  @Override
  public int hashCode() {
    return Objects.hash(editorsPick);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Metadata {\n");
    sb.append("    editorsPick: ").append(toIndentedString(editorsPick)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(@Nullable Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

