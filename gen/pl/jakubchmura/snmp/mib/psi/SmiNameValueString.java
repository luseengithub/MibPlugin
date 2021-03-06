// This is a generated file. Not intended for manual editing.
package pl.jakubchmura.snmp.mib.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import pl.jakubchmura.snmp.mib.reference.SmiReference;

public interface SmiNameValueString extends SmiIdentifiableElement {

  @NotNull
  PsiElement getIdentifierString();

  @NotNull
  String getName();

  PsiElement setName(String name);

  SmiReference getReference();

}
