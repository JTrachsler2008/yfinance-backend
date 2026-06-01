package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Thumbnail information for a news article.
 */

@Schema(name = "Thumbnail", description = "Thumbnail information for a news article.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class Thumbnail {

  private @Nullable String originalUrl = null;

  private @Nullable Integer originalWidth = null;

  private @Nullable Integer originalHeight = null;

  private @Nullable String caption = null;

  @Valid
  private @Nullable List<@Valid Resolution> resolutions;

  public Thumbnail originalUrl(@Nullable String originalUrl) {
    this.originalUrl = originalUrl;
    return this;
  }

  /**
   * Get originalUrl
   * @return originalUrl
   */
  
  @Schema(name = "originalUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalUrl")
  public @Nullable String getOriginalUrl() {
    return originalUrl;
  }

  public void setOriginalUrl(@Nullable String originalUrl) {
    this.originalUrl = originalUrl;
  }

  public Thumbnail originalWidth(@Nullable Integer originalWidth) {
    this.originalWidth = originalWidth;
    return this;
  }

  /**
   * Get originalWidth
   * @return originalWidth
   */
  
  @Schema(name = "originalWidth", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalWidth")
  public @Nullable Integer getOriginalWidth() {
    return originalWidth;
  }

  public void setOriginalWidth(@Nullable Integer originalWidth) {
    this.originalWidth = originalWidth;
  }

  public Thumbnail originalHeight(@Nullable Integer originalHeight) {
    this.originalHeight = originalHeight;
    return this;
  }

  /**
   * Get originalHeight
   * @return originalHeight
   */
  
  @Schema(name = "originalHeight", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("originalHeight")
  public @Nullable Integer getOriginalHeight() {
    return originalHeight;
  }

  public void setOriginalHeight(@Nullable Integer originalHeight) {
    this.originalHeight = originalHeight;
  }

  public Thumbnail caption(@Nullable String caption) {
    this.caption = caption;
    return this;
  }

  /**
   * Get caption
   * @return caption
   */
  
  @Schema(name = "caption", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("caption")
  public @Nullable String getCaption() {
    return caption;
  }

  public void setCaption(@Nullable String caption) {
    this.caption = caption;
  }

  public Thumbnail resolutions(@Nullable List<@Valid Resolution> resolutions) {
    this.resolutions = resolutions;
    return this;
  }

  public Thumbnail addResolutionsItem(Resolution resolutionsItem) {
    if (this.resolutions == null) {
      this.resolutions = new ArrayList<>();
    }
    this.resolutions.add(resolutionsItem);
    return this;
  }

  /**
   * Get resolutions
   * @return resolutions
   */
  @Valid 
  @Schema(name = "resolutions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("resolutions")
  public @Nullable List<@Valid Resolution> getResolutions() {
    return resolutions;
  }

  public void setResolutions(@Nullable List<@Valid Resolution> resolutions) {
    this.resolutions = resolutions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Thumbnail thumbnail = (Thumbnail) o;
    return Objects.equals(this.originalUrl, thumbnail.originalUrl) &&
        Objects.equals(this.originalWidth, thumbnail.originalWidth) &&
        Objects.equals(this.originalHeight, thumbnail.originalHeight) &&
        Objects.equals(this.caption, thumbnail.caption) &&
        Objects.equals(this.resolutions, thumbnail.resolutions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(originalUrl, originalWidth, originalHeight, caption, resolutions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Thumbnail {\n");
    sb.append("    originalUrl: ").append(toIndentedString(originalUrl)).append("\n");
    sb.append("    originalWidth: ").append(toIndentedString(originalWidth)).append("\n");
    sb.append("    originalHeight: ").append(toIndentedString(originalHeight)).append("\n");
    sb.append("    caption: ").append(toIndentedString(caption)).append("\n");
    sb.append("    resolutions: ").append(toIndentedString(resolutions)).append("\n");
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

