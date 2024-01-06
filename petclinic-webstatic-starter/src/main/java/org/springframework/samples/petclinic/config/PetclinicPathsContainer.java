package org.springframework.samples.petclinic.config;

import com.github.vssavin.usman_webstatic_core.HttpMethod;
import com.github.vssavin.usmancore.config.AuthorizedUrlPermission;
import com.github.vssavin.usmancore.config.Permission;
import com.github.vssavin.usmancore.config.PermissionPathsContainer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link com.github.vssavin.usmancore.config.PermissionPathsContainer} implementation
 * with petclinic permission paths.
 *
 * @author vssavin on 05.01.2024.
 */
@Component
public class PetclinicPathsContainer implements PermissionPathsContainer {

	private final Map<Permission, List<AuthorizedUrlPermission>> container = new EnumMap<>(Permission.class);

	public PetclinicPathsContainer() {
		List<AuthorizedUrlPermission> userAdminPaths = getUserAdminPaths();
		container.put(Permission.USER_ADMIN, userAdminPaths);
		container.put(Permission.ANY_USER, getAnyUserPaths());
		container.put(Permission.ADMIN_ONLY, new ArrayList<>());
	}

	@Override
	public List<AuthorizedUrlPermission> getPermissionPaths(Permission permission) {
		return container.get(permission);
	}

	private List<AuthorizedUrlPermission> getUserAdminPaths() {
		List<AuthorizedUrlPermission> paths = getAuthorizedPermissionsForUrl("/owners", Permission.USER_ADMIN);
		paths.addAll(getAuthorizedPermissionsForUrl("/owners/**", Permission.USER_ADMIN));
		return paths;
	}

	private List<AuthorizedUrlPermission> getAnyUserPaths() {
		List<AuthorizedUrlPermission> paths = new ArrayList<>();
		paths.add(new AuthorizedUrlPermission("/oups", Permission.ANY_USER));
		paths.add(new AuthorizedUrlPermission("/resources/css/**", Permission.ANY_USER));
		paths.add(new AuthorizedUrlPermission("/resources/fonts/**", Permission.ANY_USER));
		paths.add(new AuthorizedUrlPermission("/resources/images/**", Permission.ANY_USER));
		paths.add(new AuthorizedUrlPermission("/webjars/**", Permission.ANY_USER));
		paths.addAll(getAuthorizedPermissionsForUrl("/error", Permission.ANY_USER));
		return paths;
	}

	private List<AuthorizedUrlPermission> getAuthorizedPermissionsForUrl(String url, Permission permission) {
		List<AuthorizedUrlPermission> urlPermissions = new ArrayList<>();
		urlPermissions.add(new AuthorizedUrlPermission(url, permission));
		urlPermissions.add(new AuthorizedUrlPermission(url, HttpMethod.POST.name(), permission));
		urlPermissions.add(new AuthorizedUrlPermission(url, HttpMethod.PATCH.name(), permission));
		urlPermissions.add(new AuthorizedUrlPermission(url, HttpMethod.PUT.name(), permission));
		urlPermissions.add(new AuthorizedUrlPermission(url, HttpMethod.DELETE.name(), permission));
		return urlPermissions;
	}

}
