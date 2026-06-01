package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Storyline information for a news article.
 */

@Schema(name = "Storyline", description = "Storyline information for a news article.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class Storyline {

  @Valid
  private List<@Valid StorylineItem> storylineItems;

  public Storyline() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Storyline(List<@Valid StorylineItem> storylineItems) {
    this.storylineItems = storylineItems;
  }

  public Storyline storylineItems(List<@Valid StorylineItem> storylineItems) {
    this.storylineItems = storylineItems;
    return this;
  }

  public Storyline addStorylineItemsItem(StorylineItem storylineItemsItem) {
    if (this.storylineItems == null) {
      this.storylineItems = new ArrayList<>();
    }
    this.storylineItems.add(storylineItemsItem);
    return this;
  }

  /**
   * Get storylineItems
   * @return storylineItems
   */
  @NotNull @Valid 
  @Schema(name = "storylineItems", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("storylineItems")
  public List<@Valid StorylineItem> getStorylineItems() {
    return storylineItems;
  }

  public void setStorylineItems(List<@Valid StorylineItem> storylineItems) {
    this.storylineItems = storylineItems;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Storyline storyline = (Storyline) o;
    return Objects.equals(this.storylineItems, storyline.storylineItems);
  }

  @Override
  public int hashCode() {
    return Objects.hash(storylineItems);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Storyline {\n");
    sb.append("    storylineItems: ").append(toIndentedString(storylineItems)).append("\n");
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

