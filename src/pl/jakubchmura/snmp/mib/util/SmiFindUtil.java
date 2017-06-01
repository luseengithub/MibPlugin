package pl.jakubchmura.snmp.mib.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import pl.jakubchmura.snmp.mib.MibFileType;
import pl.jakubchmura.snmp.mib.StandardSnmpv2Mibs;
import pl.jakubchmura.snmp.mib.psi.SmiReferenceableElement;
import pl.jakubchmura.snmp.mib.psi.SmiSymbol;
import pl.jakubchmura.snmp.mib.psi.SmiSymbolName;
import pl.jakubchmura.snmp.mib.psi.SmiSymbolsFromModule;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SmiFindUtil {

    public static <T extends SmiReferenceableElement> List<T> findElements(Project project, GlobalSearchScope scope, Class<T> elementClass, String name) {
        List<T> identifiableElements = findElements(project, scope, elementClass);
        return filterMyName(identifiableElements, name);
    }

    public static <T extends SmiReferenceableElement> List<T> findElements(PsiFile file, Class<T> elementClass, String name) {
        List<T> identifiableElements = findElements(file, elementClass);
        return filterMyName(identifiableElements, name);
    }

    public static <T extends SmiReferenceableElement> List<T> findElements(Project project, GlobalSearchScope scope, Class<T> elementClass) {
        List<T> result = new ArrayList<>();
        if (scope != null) {
            Collection<VirtualFile> files = FileTypeIndex.getFiles(MibFileType.INSTANCE, scope);
            for (VirtualFile file : files) {
                result.addAll(findElements(project, file, elementClass));
            }
        }
        for (VirtualFile mibFile : StandardSnmpv2Mibs.getMibs()) {
            result.addAll(findElements(project, mibFile, elementClass));
        }
        return result;
    }

    public static <T extends SmiReferenceableElement> List<T> findElements(Project project, VirtualFile virtualFile, Class<T> elementClass) {
        PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
        if (file != null) {
            return findElements(file, elementClass);
        }
        return Collections.emptyList();
    }

    @NotNull
    public static <T extends PsiElement> List<T> findElements(PsiFile file, Class<T> elementClass) {
        return new ArrayList<>(PsiTreeUtil.collectElementsOfType(file, elementClass));
    }

    public static <T extends SmiReferenceableElement> List<T> findImportedElements(PsiFile file, Class<T> identifiableElementClass) {
        return findElements(file, SmiSymbolsFromModule.class)
                .stream()
                .flatMap(e -> e.getSymbolList().stream())
                .map(SmiSymbol::getSymbolName)
                .filter(Objects::nonNull)
                .map(SmiSymbolName::getReferences)
                .flatMap(Stream::of)
                .map(r -> r.multiResolve(false))
                .flatMap(Stream::of)
                .map(ResolveResult::getElement)
                .filter(Objects::nonNull)
                .filter(e -> identifiableElementClass.isAssignableFrom(e.getClass()))
                .map(identifiableElementClass::cast)
                .collect(Collectors.toList());
    }

    public static List<SmiReferenceableElement> findReferenceableElements(PsiFile file) {
        return findElements(file, SmiReferenceableElement.class);
    }

    public static <T extends SmiReferenceableElement> List<T> findImportedElements(PsiFile file, Class<T> identifiableElementClass, String name) {
        return filterMyName(findImportedElements(file, identifiableElementClass), name);
    }

    public static <T extends PsiElement> List<T> filterOutStandardMibs(List<T> elements) {
        List<VirtualFile> standardMibs = StandardSnmpv2Mibs.getMibs();
        List<T> standardElements = new ArrayList<>();
        List<T> customElements = new ArrayList<>();

        for (T element : elements) {
            VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
            if (standardMibs.contains(virtualFile)) {
                standardElements.add(element);
            } else {
                customElements.add(element);
            }
        }

        if (!customElements.isEmpty()) {
            return customElements;
        } else {
            return standardElements;
        }
    }


    private static <T extends SmiReferenceableElement> List<T> filterMyName(List<T> identifiableElements, String name) {
        return identifiableElements.stream().filter(e -> name.equals(e.getName())).collect(Collectors.toList());
    }
}
