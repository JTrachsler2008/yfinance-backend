package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Content of a news article.
 */

@Schema(name = "Content", description = "Content of a news article.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class Content {

  private String id = null;

  private @Nullable String contentType = null;

  private @Nullable String title = null;

  private @Nullable String description = null;

  private @Nullable String summary = null;

  private @Nullable String pubDate = null;

  private @Nullable String displayTime = null;

  private @Nullable Boolean isHosted = null;

  private @Nullable Boolean bypassModal = null;

  private @Nullable String previewUrl = null;

  private @Nullable Thumbnail thumbnail = null;

  private @Nullable Provider provider = null;

  private @Nullable CanonicalUrl canonicalUrl = null;

  private @Nullable ClickThroughUrl clickThroughUrl = null;

  private @Nullable Metadata metadata = null;

  private @Nullable Finance finance = null;

  private @Nullable Storyline storyline = null;

  public Content() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Content(String id) {
    this.id = id;
  }

  public Content id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
   */
  @NotNull 
  @Schema(name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Content contentType(@Nullable String contentType) {
    this.contentType = contentType;
    return this;
  }

  /**
   * Get contentType
   * @return contentType
   */
  
  @Schema(name = "contentType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("contentType")
  public @Nullable String getContentType() {
    return contentType;
  }

  public void setContentType(@Nullable String contentType) {
    this.contentType = contentType;
  }

  public Content title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
   */
  
  @Schema(name = "title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  public Content description(@Nullable String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
   */
  
  @Schema(name = "description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("description")
  public @Nullable String getDescription() {
    return description;
  }

  public void setDescription(@Nullable String description) {
    this.description = description;
  }

  public Content summary(@Nullable String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * Get summary
   * @return summary
   */
  
  @Schema(name = "summary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("summary")
  public @Nullable String getSummary() {
    return summary;
  }

  public void setSummary(@Nullable String summary) {
    this.summary = summary;
  }

  public Content pubDate(@Nullable String pubDate) {
    this.pubDate = pubDate;
    return this;
  }

  /**
   * Get pubDate
   * @return pubDate
   */
  
  @Schema(name = "pubDate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pubDate")
  public @Nullable String getPubDate() {
    return pubDate;
  }

  public void setPubDate(@Nullable String pubDate) {
    this.pubDate = pubDate;
  }

  public Content displayTime(@Nullable String displayTime) {
    this.displayTime = displayTime;
    return this;
  }

  /**
   * Get displayTime
   * @return displayTime
   */
  
  @Schema(name = "displayTime", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("displayTime")
  public @Nullable String getDisplayTime() {
    return displayTime;
  }

  public void setDisplayTime(@Nullable String displayTime) {
    this.displayTime = displayTime;
  }

  public Content isHosted(@Nullable Boolean isHosted) {
    this.isHosted = isHosted;
    return this;
  }

  /**
   * Get isHosted
   * @return isHosted
   */
  
  @Schema(name = "isHosted", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("isHosted")
  public @Nullable Boolean getIsHosted() {
    return isHosted;
  }

  public void setIsHosted(@Nullable Boolean isHosted) {
    this.isHosted = isHosted;
  }

  public Content bypassModal(@Nullable Boolean bypassModal) {
    this.bypassModal = bypassModal;
    return this;
  }

  /**
   * Get bypassModal
   * @return bypassModal
   */
  
  @Schema(name = "bypassModal", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bypassModal")
  public @Nullable Boolean getBypassModal() {
    return bypassModal;
  }

  public void setBypassModal(@Nullable Boolean bypassModal) {
    this.bypassModal = bypassModal;
  }

  public Content previewUrl(@Nullable String previewUrl) {
    this.previewUrl = previewUrl;
    return this;
  }

  /**
   * Get previewUrl
   * @return previewUrl
   */
  
  @Schema(name = "previewUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("previewUrl")
  public @Nullable String getPreviewUrl() {
    return previewUrl;
  }

  public void setPreviewUrl(@Nullable String previewUrl) {
    this.previewUrl = previewUrl;
  }

  public Content thumbnail(@Nullable Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
    return this;
  }

  /**
   * Get thumbnail
   * @return thumbnail
   */
  @Valid 
  @Schema(name = "thumbnail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("thumbnail")
  public @Nullable Thumbnail getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(@Nullable Thumbnail thumbnail) {
    this.thumbnail = thumbnail;
  }

  public Content provider(@Nullable Provider provider) {
    this.provider = provider;
    return this;
  }

  /**
   * Get provider
   * @return provider
   */
  @Valid 
  @Schema(name = "provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("provider")
  public @Nullable Provider getProvider() {
    return provider;
  }

  public void setProvider(@Nullable Provider provider) {
    this.provider = provider;
  }

  public Content canonicalUrl(@Nullable CanonicalUrl canonicalUrl) {
    this.canonicalUrl = canonicalUrl;
    return this;
  }

  /**
   * Get canonicalUrl
   * @return canonicalUrl
   */
  @Valid 
  @Schema(name = "canonicalUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("canonicalUrl")
  public @Nullable CanonicalUrl getCanonicalUrl() {
    return canonicalUrl;
  }

  public void setCanonicalUrl(@Nullable CanonicalUrl canonicalUrl) {
    this.canonicalUrl = canonicalUrl;
  }

  public Content clickThroughUrl(@Nullable ClickThroughUrl clickThroughUrl) {
    this.clickThroughUrl = clickThroughUrl;
    return this;
  }

  /**
   * Get clickThroughUrl
   * @return clickThroughUrl
   */
  @Valid 
  @Schema(name = "clickThroughUrl", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("clickThroughUrl")
  public @Nullable ClickThroughUrl getClickThroughUrl() {
    return clickThroughUrl;
  }

  public void setClickThroughUrl(@Nullable ClickThroughUrl clickThroughUrl) {
    this.clickThroughUrl = clickThroughUrl;
  }

  public Content metadata(@Nullable Metadata metadata) {
    this.metadata = metadata;
    return this;
  }

  /**
   * Get metadata
   * @return metadata
   */
  @Valid 
  @Schema(name = "metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("metadata")
  public @Nullable Metadata getMetadata() {
    return metadata;
  }

  public void setMetadata(@Nullable Metadata metadata) {
    this.metadata = metadata;
  }

  public Content finance(@Nullable Finance finance) {
    this.finance = finance;
    return this;
  }

  /**
   * Get finance
   * @return finance
   */
  @Valid 
  @Schema(name = "finance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("finance")
  public @Nullable Finance getFinance() {
    return finance;
  }

  public void setFinance(@Nullable Finance finance) {
    this.finance = finance;
  }

  public Content storyline(@Nullable Storyline storyline) {
    this.storyline = storyline;
    return this;
  }

  /**
   * Get storyline
   * @return storyline
   */
  @Valid 
  @Schema(name = "storyline", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("storyline")
  public @Nullable Storyline getStoryline() {
    return storyline;
  }

  public void setStoryline(@Nullable Storyline storyline) {
    this.storyline = storyline;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Content content = (Content) o;
    return Objects.equals(this.id, content.id) &&
        Objects.equals(this.contentType, content.contentType) &&
        Objects.equals(this.title, content.title) &&
        Objects.equals(this.description, content.description) &&
        Objects.equals(this.summary, content.summary) &&
        Objects.equals(this.pubDate, content.pubDate) &&
        Objects.equals(this.displayTime, content.displayTime) &&
        Objects.equals(this.isHosted, content.isHosted) &&
        Objects.equals(this.bypassModal, content.bypassModal) &&
        Objects.equals(this.previewUrl, content.previewUrl) &&
        Objects.equals(this.thumbnail, content.thumbnail) &&
        Objects.equals(this.provider, content.provider) &&
        Objects.equals(this.canonicalUrl, content.canonicalUrl) &&
        Objects.equals(this.clickThroughUrl, content.clickThroughUrl) &&
        Objects.equals(this.metadata, content.metadata) &&
        Objects.equals(this.finance, content.finance) &&
        Objects.equals(this.storyline, content.storyline);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, contentType, title, description, summary, pubDate, displayTime, isHosted, bypassModal, previewUrl, thumbnail, provider, canonicalUrl, clickThroughUrl, metadata, finance, storyline);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Content {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    pubDate: ").append(toIndentedString(pubDate)).append("\n");
    sb.append("    displayTime: ").append(toIndentedString(displayTime)).append("\n");
    sb.append("    isHosted: ").append(toIndentedString(isHosted)).append("\n");
    sb.append("    bypassModal: ").append(toIndentedString(bypassModal)).append("\n");
    sb.append("    previewUrl: ").append(toIndentedString(previewUrl)).append("\n");
    sb.append("    thumbnail: ").append(toIndentedString(thumbnail)).append("\n");
    sb.append("    provider: ").append(toIndentedString(provider)).append("\n");
    sb.append("    canonicalUrl: ").append(toIndentedString(canonicalUrl)).append("\n");
    sb.append("    clickThroughUrl: ").append(toIndentedString(clickThroughUrl)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    finance: ").append(toIndentedString(finance)).append("\n");
    sb.append("    storyline: ").append(toIndentedString(storyline)).append("\n");
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

