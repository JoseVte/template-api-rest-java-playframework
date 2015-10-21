class mysql {

  package {
    "mysql-server":ensure => installed;
    "mysql-client":ensure => installed;
  }

  service { "mysql":
    enable => true,
    ensure => running,
    require => Package["mysql-server"],
  }

  file { "/etc/mysql/my.cnf":
    ensure  => 'link',
    force   => true,
    replace => true,
    source => "/vagrant/etc/vagrant/modules/mysql/files/my.cnf",
    require => Package["mysql-server"],
    notify  => Service["mysql"],
  }

  exec { "set-mysql-password":
    unless => "mysqladmin -uroot status",
    path => ["/bin", "/usr/bin"],
    command => "mysqladmin -uroot password ''",
    require => Service["mysql"],
  }

  exec { "create-db":
    command => "/usr/bin/mysql -u root < /vagrant/etc/vagrant/modules/mysql/files/database.sql",
    require => Service["mysql"],
  }
}