call git checkout cssbox-vendor
call git svn rebase

call git checkout jstyleparser-vendor
call git svn rebase

call git checkout swingbox-vendor
call git svn rebase

call git checkout master

call git merge --no-ff -Xsubtree=src/main/java cssbox-vendor
call git merge --no-ff -Xsubtree=src/main/java jstyleparser-vendor
call git merge --no-ff -Xsubtree=src/main/java swingbox-vendor

pause
