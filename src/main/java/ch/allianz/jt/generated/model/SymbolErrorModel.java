package ch.allianz.jt.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Per-symbol error shape for bulk quote responses.
 */

@Schema(name = "SymbolErrorModel", description = "Per-symbol error shape for bulk quote responses.")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-02-13T08:57:23.507268700+01:00[Europe/Zurich]", comments = "Generator version: 7.19.0")
public class SymbolErrorModel {

  private String error;

  private Integer statusCode;

  public SymbolErrorModel() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SymbolErrorModel(String error, Integer statusCode) {
    this.error = error;
    this.statusCode = statusCode;
  }

  public SymbolErrorModel error(String error) {
    this.error = error;
    return this;
  }

  /**
   * Get error
   * @return error
   */
  @NotNull 
  @Schema(name = "error", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("error")
  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public SymbolErrorModel statusCode(Integer statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * Get statusCode
   * @return statusCode
   */
  @NotNull 
  @Schema(name = "status_code", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("status_code")
  public Integer getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SymbolErrorModel symbolErrorModel = (SymbolErrorModel) o;
    return Objects.equals(this.error, symbolErrorModel.error) &&
        Objects.equals(this.statusCode, symbolErrorModel.statusCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(error, statusCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SymbolErrorModel {\n");
    sb.append("    error: ").append(toIndentedString(error)).append("\n");
    sb.append("    statusCode: ").append(toIndentedString(statusCode)).append("\n");
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

