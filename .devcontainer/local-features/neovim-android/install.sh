#!/usr/bin/env bash
#-------------------------------------------------------------------------------------------------------------
# Copyright (c) vutrankien.vn@gmail.com. All rights reserved.
# Licensed under the MIT License. See https://go.microsoft.com/fwlink/?linkid=2090316 for license information.
#-------------------------------------------------------------------------------------------------------------

USERNAME=${USERNAME:-"codespace"}

#set -eux

if [ "$(id -u)" -ne 0 ]; then
  echo -e 'Script must be run as root. Use sudo, su, or add "USER root" to your Dockerfile before running this script.'
  exit 1
fi

export DEBIAN_FRONTEND=noninteractive

sudo_if() {
  COMMAND="$*"
  if [ "$(id -u)" -eq 0 ] && [ "$USERNAME" != "root" ]; then
    su - "$USERNAME" -c "$COMMAND"
  else
    "$COMMAND"
  fi
}

wget $ANDROID_STUDIO_URL
file=$(basename $ANDROID_STUDIO_URL)
tar -xz -C /opt/ -f $file
#cd /opt
ls /opt/android-studio
echo "android-studio installed in /opt/android-studio!"

wget $NEOVIM_URL
file=$(basename $NEOVIM_URL)
chmod -v +x $file
mv -v $file /usr/local/bin/nvim
# Install fuse since nvim is in AppImage format.
# netcat for connecting to LSP server via TCP.
apt update
apt install fuse netcat

#lazygit
mkdir /tmp/lazygit
cd /tmp/lazygit
LAZYGIT_VERSION=$(curl -s "https://api.github.com/repos/jesseduffield/lazygit/releases/latest" | \grep -Po '"tag_name": *"v\K[^"]*')
curl -Lo lazygit.tar.gz "https://github.com/jesseduffield/lazygit/releases/download/v${LAZYGIT_VERSION}/lazygit_${LAZYGIT_VERSION}_Linux_x86_64.tar.gz"
tar xf lazygit.tar.gz lazygit
install lazygit -D -t /usr/local/bin/

apt install tmux

sudo_if git clone --depth 1 https://github.com/KentVu/LazyVim-starter.git /home/$USERNAME/.config/nvim -b personal
sudo_if git clone --depth 1 https://github.com/junegunn/fzf.git /home/$USERNAME/.fzf
#need interaction!!  sudo_if /home/$USERNAME/.fzf/install --no-key-bindings

echo "TODO:
- ~/.fzf/install --no-key-bindings
- gh run download --repo KentVu/Ideals2 14214649733
- Install android-studio plugin
- echo '[exec] (Android Studio) { /opt/android-studio/bin/studio } <>' >> ~/.fluxbox/menu
- ln -s .config/nvim/.ideavimrc .
- copy adb.debug keystore: gh codespace cp -c symmetrical-spoon-xxx -e ~/.android/debug.keystore 'remote:~/.android'"
