Release steps for Ashley:

1. Make sure [`publish.gradle`](https://github.com/libgdx/ashley/blob/master/publish.gradle) has the correct version. API breaking releases cannot just increase the patch version.
2. Create [tag](https://github.com/libgdx/ashley/releases) with the new version.
3. Run release script and publish on Maven.
4. Increase patch version for the next release.
5. [Blog post](http://saltares.com/blog/projects/ashley-1-7-0-released/) with changes.
6. Update Ashley version on [gdx-setup](https://github.com/libgdx/libgdx/blob/master/extensions/gdx-setup/src/com/badlogic/gdx/setup/DependencyBank.java).