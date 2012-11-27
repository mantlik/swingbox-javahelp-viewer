call git checkout cssbox-vendor
call git svn rebase

call git checkout jstyleparser-vendor
call git svn rebase

call git checkout swingbox-vendor
call git svn rebase

call git checkout master

call git merge --no-ff -s subtree cssbox-vendor
call git merge --no-ff -s subtree jstyleparser-vendor
call git merge --no-ff -s subtree swingbox-vendor

pause
