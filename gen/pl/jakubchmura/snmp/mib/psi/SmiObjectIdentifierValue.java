// This is a generated file. Not intended for manual editing.
package pl.jakubchmura.snmp.mib.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SmiObjectIdentifierValue extends PsiElement {

  @NotNull
  List<SmiNameAndNumber> getNameAndNumberList();

  @NotNull
  List<SmiNameValueIndex> getNameValueIndexList();

  @NotNull
  List<SmiNameValueString> getNameValueStringList();

}
