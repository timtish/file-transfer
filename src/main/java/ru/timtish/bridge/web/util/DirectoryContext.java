package ru.timtish.bridge.web.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;

import org.apache.naming.NamingEntry;
import org.apache.naming.resources.BaseDirContext;
import org.apache.naming.resources.Resource;
import org.apache.naming.resources.ResourceAttributes;
import ru.timtish.bridge.box.*;
import ru.timtish.bridge.pipeline.converter.Zip;

/**
 * @author Timofey Tishin (ttishin@luxoft.com)
 */
public class DirectoryContext extends BaseDirContext {

	private BoxEntity dir;
	private static final byte[] DESKTOP_INI = ("[.ShellClassInfo]\n" +
			"LocalizedResourceName=@%SystemRoot%\\system32\\shell32.dll,-21798\n" +
			"NoSharing=0\n" +
			"Sharing=1\n" +
			"InfoTip=\"InfoTip\"\n" +
			"IconResource=%SystemRoot%\\system32\\imageres.dll,-184").getBytes();

	public DirectoryContext(BoxEntity dir) {
		this.dir = dir;
	}

	@Override
	public void unbind(String name) throws NamingException {
		System.out.println(">unbind " + name);
	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		System.out.println(">rename " + oldName + "->" + newName);
	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		System.out.println(">destroySubcontext " + name);
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		System.out.println(">lookupLink " + name);
		return null;
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		System.out.println(">getNameInNamespace");
		return null;
	}

	@Override
	public void modifyAttributes(String name, int mod_op, Attributes attrs) throws NamingException {
		System.out.println(">modifyAttributes " + name + " " + mod_op);
	}

	@Override
	public void modifyAttributes(String name, ModificationItem[] mods) throws NamingException {
		System.out.println(">modifyAttributes " + name);
	}

	@Override
	public void bind(String name, Object obj, Attributes attrs) throws NamingException {
		System.out.println(">bind " + name + " " + obj);
		if (obj instanceof Resource) {
			// todo: upload file
		}
	}

	@Override
	public void rebind(String name, Object obj, Attributes attrs) throws NamingException {
		System.out.println(">rebind " + name + " " + obj);
	}

	@Override
	public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
		System.out.println(">createSubcontext" + name + " " + attrs.size());
		return null;
	}

	@Override
	public DirContext getSchema(String name) throws NamingException {
		System.out.println(">getSchema " + name);
		return null;
	}

	@Override
	public DirContext getSchemaClassDefinition(String name) throws NamingException {
		System.out.println(">getSchemaClassDefinition " + name);
		return this;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
		System.out.println(">search1 " + name);
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
		System.out.println(">search2 " + name);
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
		System.out.println(">search3 " + name);
		return null;
	}

	@Override
	public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
		System.out.println(">search4 " + name);
		return null;
	}

	@Override
	protected Attributes doGetAttributes(String name, String[] attrIds) throws NamingException {

		if (name.endsWith("/desktop.ini")) {
			ResourceAttributes result = new ResourceAttributes();
			result.setName("desktop.ini");
			result.setCollection(false);
			result.setContentLength(DESKTOP_INI.length);
			System.out.println(">attributes " + name + " ok");
			return result;
		}

		BoxEntity entity = dir.getChild(name);
		if (entity != null) {
			ResourceAttributes result = new ResourceAttributes();
			result.setName(entity.getName());
			result.setCreationDate(entity.getDate());
			result.setContentLength(entity.getSize());
			result.setCollection(entity.isContainer());
			System.out.println(">attributes " + name + " : " + entity.getName() + " " + entity.isContainer() + " " + entity.getDate() + " " + entity.getSize());
			return result;
		} else {
			System.out.println(">attributes " + name + " not found");
			return null;
		}
	}

	@Override
	protected Object doLookup(String name) {
		Object result = null;

		if (name.endsWith("/desktop.ini")) return new Resource(DESKTOP_INI);

		BoxEntity entity = dir.getChild(name);
		if (entity != null) {
			result = doLookup(entity);
			if (result instanceof DirectoryContext) {
				((DirectoryContext) result).setDocBase(dir.getName());
			}
		}

		System.out.println(">lookup " + dir.getName() + " " + name + " : " + result);
		return result;
	}

	protected Object doLookup(BoxEntity entity) {
		if (entity.isContainer()) {
			return new DirectoryContext(entity);
		} else if (entity instanceof BoxFile) {
			BoxFile file = (BoxFile) entity;
			if (file.getInputStream().isRepeatable()) {
				return new Resource(file.getInputStream().createCopy());
			} else {
				return null; // todo: один раз поток можно вернуть
			}
		} else if (entity instanceof BoxZipFile) {
			BoxZipFile file = (BoxZipFile) entity;
			try {
				return new Resource(Zip.unzip(file.getInputStream().createCopy(), file.getZipFilePath())); // todo: lazy unzip
			} catch (IOException e) {
				e.printStackTrace();
				return null; // todo
			}
		} else {
			return null;
		}
	}

	@Override
	protected List<NamingEntry> doListBindings(String name) throws NamingException {
		List<NamingEntry> list = new ArrayList<NamingEntry>();

		BoxEntity file = dir.getChild(name);
		if (file != null) {
			for (BoxEntity child : file.getChilds()) {
				if (child.isContainer()) {
					DirectoryContext val = new DirectoryContext(child);
					val.setDocBase(file.getName());
					list.add(new NamingEntry(child.getName(), val, NamingEntry.CONTEXT));
				} else {
					//if (!StreamStatus.CLOSED.equals(((BoxFile) child).getInputStream().getStatus())) {
					list.add(new NamingEntry(child.getName(), doLookup(child), NamingEntry.ENTRY));
				}
			}
			String names = "";
			for (NamingEntry ne : list) names += " " + ne.name + "[" + ne.value + "]";
			System.out.println(">list " + dir.getName() + " " + name + " : " + names);
		} else {
			System.out.println(">list " + dir.getName() + " " + name + " not found");
		}
		return list;
	}

	@Override
	protected String doGetRealPath(String name) {
		System.out.println(">doGetRealPath " + name);
		return null;
	}

	@Override
	public String toString() {
		return "DirectoryContext{" +
				"dir=" + dir.getName() +
				'}';
	}
}
